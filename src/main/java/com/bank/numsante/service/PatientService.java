package com.bank.numsante.service;

import com.bank.numsante.dto.EnregistrementPatientRequest;
import com.bank.numsante.dto.HistoriquePassageDto;
import com.bank.numsante.entity.CarteNumerique;
import com.bank.numsante.entity.PassageMedical;
import com.bank.numsante.entity.Patient;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.repository.CarteNumeriqueRepository;
import com.bank.numsante.repository.PassageMedicalRepository;
import com.bank.numsante.repository.PatientRepository;
import com.bank.numsante.repository.PersonnelMedicalRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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