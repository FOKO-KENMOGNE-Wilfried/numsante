package com.bank.numsante.service;

import com.bank.numsante.dto.CreerPassageRequest;
import com.bank.numsante.dto.PatientInfoDto;
import com.bank.numsante.dto.QrScanRequest;
import com.bank.numsante.entity.*;
import com.bank.numsante.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdmissionService {

    private final CarteNumeriqueRepository carteRepo;
    private final PatientRepository patientRepo;
    private final HopitalRepository hopitalRepo;
    private final PersonnelMedicalRepository personnelRepo;
    private final PassageMedicalRepository passageRepo;
    private final LogService logService;
    private final HttpServletRequest httpServletRequest;

    public PatientInfoDto scanCarte(QrScanRequest request) {
        CarteNumerique carte = carteRepo.findByQrCodeToken(request.getQrCodeToken())
                .orElseThrow(() -> new RuntimeException("Carte invalide ou inexistante"));
        Patient patient = carte.getPatient();
        // Log
        logService.logAction(null, patient.getIdPatient(), "SCAN_QR_CODE", null);
        return new PatientInfoDto(
                patient.getIdPatient(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getDateNaissance(),
                patient.getGroupeSanguin()
        );
    }

    @Transactional
    public UUID creerPassage(CreerPassageRequest request, String username) {
        Patient patient = patientRepo.findById(UUID.fromString(request.getIdPatient()))
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        Hopital hopital = hopitalRepo.findById(request.getIdHopital())
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé"));
        PersonnelMedical createur = personnelRepo.findByIdentifiantPro(username)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        PassageMedical passage = new PassageMedical();
        passage.setPatient(patient);
        passage.setHopital(hopital);
        passage.setCreateur(createur);
        passage.setMotifVisite(request.getMotifVisite());
        passage.setStatutPassage("en_cours");
        passage = passageRepo.save(passage);

        logService.logAction(createur.getIdPersonnel(), patient.getIdPatient(),
                "CREATION_PASSAGE", passage.getIdPassage());
        return passage.getIdPassage();
    }
}