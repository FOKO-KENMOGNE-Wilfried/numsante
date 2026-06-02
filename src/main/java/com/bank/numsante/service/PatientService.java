package com.bank.numsante.service;

import com.bank.numsante.dto.EnregistrementPatientRequest;
import com.bank.numsante.dto.HistoriquePassageDto;
import com.bank.numsante.dto.PageResponse;
import com.bank.numsante.dto.UpdatePatientRequest;
import com.bank.numsante.entity.CarteNumerique;
import com.bank.numsante.entity.PassageMedical;
import com.bank.numsante.entity.Patient;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.exception.ResourceNotFoundException;
import com.bank.numsante.repository.CarteNumeriqueRepository;
import com.bank.numsante.repository.PassageMedicalRepository;
import com.bank.numsante.repository.PatientRepository;
import com.bank.numsante.repository.PersonnelMedicalRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PassageMedicalRepository passageRepo;
    private final LogService logService;
    private final PersonnelMedicalRepository personnelMedicalRepository;
    private final CarteNumeriqueRepository carteNumeriqueRepository;
    private final PatientRepository patientRepository;
    private final CarteNumeriqueRepository carteRepository;
    private final PersonnelMedicalRepository personnelRepo;

    public PageResponse<Patient> getAllPatients(int page, int size, String search) {
        Page<Patient> patientPage;
        if (search != null && !search.isEmpty()) {
            patientPage = patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                    search, search, PageRequest.of(page, size));
        } else {
            patientPage = patientRepository.findAll(PageRequest.of(page, size));
        }

        return PageResponse.<Patient>builder()
                .content(patientPage.getContent())
                .page(patientPage.getNumber())
                .size(patientPage.getSize())
                .totalElements(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .last(patientPage.isLast())
                .first(patientPage.isFirst())
                .build();
    }

    @Transactional
    public Patient updatePatient(UUID idPatient, UpdatePatientRequest request) {
        Patient patient = patientRepository.findById(idPatient)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));

        if (request.getNom() != null) patient.setNom(request.getNom());
        if (request.getPrenom() != null) patient.setPrenom(request.getPrenom());
        if (request.getDateNaissance() != null) patient.setDateNaissance(request.getDateNaissance());
        if (request.getTelephone() != null) patient.setTelephone(request.getTelephone());
        if (request.getGroupeSanguin() != null) patient.setGroupeSanguin(request.getGroupeSanguin());

        return patientRepository.save(patient);
    }

    public List<Patient> searchPatients(String query) {
        return patientRepository.findByNomContainingIgnoreCase(query);
    }

    public Patient getPatientById(UUID idPatient) {
        return patientRepository.findById(idPatient)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));
    }

    @Transactional
    public Map<String, Object> renouvelerCarte(UUID idPatient) {
        Patient patient = patientRepository.findById(idPatient)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));

        if (patient.getCarteNumerique() != null) {
            // Désactiver l'ancienne carte
            patient.getCarteNumerique().setStatut("remplacee");
            carteRepository.save(patient.getCarteNumerique());
        }

        // Générer une nouvelle carte
        String qrToken = genererTokenQR(patient.getIdPatient());
        CarteNumerique nouvelleCarte = new CarteNumerique();
        nouvelleCarte.setPatient(patient);
        nouvelleCarte.setQrCodeToken(qrToken);
        nouvelleCarte.setStatut("actif");
        nouvelleCarte.setExpireLe(LocalDate.now().plusYears(2));
        carteRepository.save(nouvelleCarte);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Carte renouvelée avec succès");
        response.put("qrCodeToken", qrToken);
        response.put("dateExpiration", nouvelleCarte.getExpireLe());
        return response;
    }

    public Map<String, Object> suspendreCarte(UUID idPatient, String motif) {
        Patient patient = patientRepository.findById(idPatient)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));

        if (patient.getCarteNumerique() == null) {
            throw new RuntimeException("Aucune carte trouvée pour ce patient");
        }

        patient.getCarteNumerique().setStatut(motif); // suspendu ou perdu
        carteRepository.save(patient.getCarteNumerique());

        return Map.of("message", "Carte " + motif + " avec succès");
    }

    public List<HistoriquePassageDto> getHistorique(UUID idPatient, String username) {
        List<PassageMedical> passages = passageRepo.findByPatient_IdPatientOrderByDateAdmissionDesc(idPatient);
        // Log de la consultation de l'historique
        logService.logAction(null, idPatient, "LECTURE_HISTORIQUE", null);
        return passages.stream()
                .map(p -> new HistoriquePassageDto(
                        p.getIdPassage(),
                        p.getHopital().getNom(),
                        p.getDateAdmission(),
                        p.getMotifVisite(),
                        p.getConstantesVitales(),
                        p.getDiagnostic(),
                        p.getPrescriptionOrdonnance(),
                        p.getStatutPassage()
                ))
                .collect(Collectors.toList());
    }

    // PatientService.java - Ajouter cette méthode
    @Transactional
    public Map<String, Object> enregistrerPatient(EnregistrementPatientRequest request, String username) {
        // 1. Créer le patient
        Patient patient = new Patient();
        patient.setNom(request.getNom());
        patient.setPrenom(request.getPrenom());
        patient.setDateNaissance(request.getDateNaissance());
        patient.setGenre(request.getGenre());
        patient.setGroupeSanguin(request.getGroupeSanguin());
        patient.setTelephone(request.getTelephone());
        patient = patientRepository.save(patient);

        // 2. Générer un token unique pour le QR code
        String qrToken = genererTokenQR(patient.getIdPatient());

        // 3. Créer la carte numérique
        CarteNumerique carte = new CarteNumerique();
        carte.setPatient(patient);
        carte.setQrCodeToken(qrToken);
        carte.setStatut("actif");
        carte.setExpireLe(LocalDate.now().plusYears(2)); // Valide 2 ans
        carteNumeriqueRepository.save(carte);

        // 4. Log
        PersonnelMedical personnel = personnelMedicalRepository.findByIdentifiantPro(username)
                .orElse(null);
        logService.logAction(
                personnel != null ? personnel.getIdPersonnel() : null,
                patient.getIdPatient(),
                "CREATION_PATIENT_ET_QR",
                null
        );

        // 5. Retourner les infos + token QR (pour génération du QR code)
        Map<String, Object> response = new HashMap<>();
        response.put("idPatient", patient.getIdPatient());
        response.put("nom", patient.getNom());
        response.put("prenom", patient.getPrenom());
        response.put("qrCodeToken", qrToken);
        response.put("qrCodeUrl", "/api/v1/patients/" + patient.getIdPatient() + "/qr-code");
        response.put("dateExpiration", carte.getExpireLe());
        response.put("message", "Patient enregistré avec succès. QR code généré.");

        return response;
    }

    private String genererTokenQR(UUID patientId) {
        // Token unique = UUID + timestamp + hash sécurisé
        String rawToken = patientId.toString() + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID();
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rawToken.getBytes());
    }

    // PatientService.java - Ajouter cette méthode
    public byte[] genererImageQR(String token) {
        try {
            int width = 300;
            int height = 300;

            BitMatrix bitMatrix = new QRCodeWriter().encode(
                    token,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }

}