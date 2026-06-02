package com.bank.numsante.service;

import com.bank.numsante.dto.PrescriptionValidationRequest;
import com.bank.numsante.entity.PassageMedical;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.entity.PrescriptionMedicament;
import com.bank.numsante.repository.PassageMedicalRepository;
import com.bank.numsante.repository.PersonnelMedicalRepository;
import com.bank.numsante.repository.PrescriptionMedicamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PharmacieService {

    private final PrescriptionMedicamentRepository prescriptionRepo;
    private final PassageMedicalRepository passageRepo;
    private final PersonnelMedicalRepository personnelRepo;
    private final LogService logService;

    @Transactional
    public void validerPrescription(PrescriptionValidationRequest request, String username) {
        PassageMedical passage = passageRepo.findById(UUID.fromString(request.getIdPassage()))
                .orElseThrow(() -> new RuntimeException("Passage non trouvé"));

        PersonnelMedical pharmacien = personnelRepo.findByIdentifiantPro(username)
                .orElseThrow(() -> new RuntimeException("Pharmacien non trouvé"));

        // Extraire les médicaments de l'ordonnance
        String ordonnance = passage.getPrescriptionOrdonnance();
        if (ordonnance == null || ordonnance.isEmpty()) {
            throw new RuntimeException("Aucune ordonnance trouvée pour ce passage");
        }

        // Créer une entrée de prescription pour chaque médicament
        PrescriptionMedicament prescription = new PrescriptionMedicament();
        prescription.setPassage(passage);
        prescription.setPharmacien(pharmacien);
        prescription.setMedicament(ordonnance);
        prescription.setDelivre(request.isDelivre());
        prescription.setCommentaire(request.getCommentaire());

        prescriptionRepo.save(prescription);

        logService.logAction(pharmacien.getIdPersonnel(),
                passage.getPatient().getIdPatient(),
                request.isDelivre() ? "PRESCRIPTION_DELIVREE" : "PRESCRIPTION_REFUSEE",
                passage.getIdPassage());
    }

    public List<PrescriptionMedicament> getPrescriptionsParPassage(UUID idPassage) {
        return prescriptionRepo.findByPassage_IdPassage(idPassage);
    }

    public List<PrescriptionMedicament> getPrescriptionsEnAttente() {
        return prescriptionRepo.findByDelivreFalse();
    }
}