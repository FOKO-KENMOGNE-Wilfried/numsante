package com.bank.numsante.service;

import com.bank.numsante.dto.*;
import com.bank.numsante.entity.*;
import com.bank.numsante.repository.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final CarteNumeriqueRepository carteRepository;
    private final PassageMedicalRepository passageRepo;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Transactional
    public Map<String, Object> registerPatient(RegisterPatientRequest request) {
        if (patientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Un patient avec cet email existe déjà");
        }

        Patient patient = new Patient();
        patient.setNom(request.getNom());
        patient.setPrenom(request.getPrenom());
        patient.setDateNaissance(request.getDateNaissance());
        patient.setGenre(request.getGenre());
        patient.setGroupeSanguin(request.getGroupeSanguin());
        patient.setTelephone(request.getTelephone());
        patient.setEmail(request.getEmail());
        patient.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));
        patient = patientRepository.save(patient);

        // Génération de la carte numérique QR
        String qrToken = genererTokenQR(patient.getIdPatient());
        CarteNumerique carte = new CarteNumerique();
        carte.setPatient(patient);
        carte.setQrCodeToken(qrToken);
        carte.setStatut("actif");
        carte.setExpireLe(LocalDate.now().plusYears(2));
        carteRepository.save(carte);

        Map<String, Object> response = new HashMap<>();
        response.put("idPatient", patient.getIdPatient());
        response.put("nom", patient.getNom());
        response.put("prenom", patient.getPrenom());
        response.put("email", patient.getEmail());
        response.put("qrCodeToken", qrToken);
        response.put("message", "Patient enregistré avec succès.");
        return response;
    }

    public List<HistoriquePassageDto> getHistorique(UUID idPatient) {
        List<PassageMedical> passages = passageRepo.findByPatient_IdPatientOrderByDateAdmissionDesc(idPatient);
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
                .toList();
    }

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
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

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
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
    }

    @Transactional
    public Map<String, Object> renouvelerCarte(UUID idPatient) {
        Patient patient = getPatientById(idPatient);
        if (patient.getCarteNumerique() != null) {
            patient.getCarteNumerique().setStatut("remplacee");
            carteRepository.save(patient.getCarteNumerique());
        }

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
        response.put("dateExpiration", nouvelleCarte.getExpireLe().toString());
        return response;
    }

    @Transactional
    public Map<String, Object> suspendreCarte(UUID idPatient, String motif) {
        Patient patient = getPatientById(idPatient);
        if (patient.getCarteNumerique() == null) {
            throw new RuntimeException("Aucune carte trouvée");
        }
        patient.getCarteNumerique().setStatut(motif);
        carteRepository.save(patient.getCarteNumerique());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Carte " + motif + " avec succès");
        return response;
    }

    public byte[] genererImageQR(String texte) {
        int width = 300;
        int height = 300;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(texte, BarcodeFormat.QR_CODE, width, height);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }

    private String genererTokenQR(UUID patientId) {
        String raw = patientId.toString() + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
    }
}