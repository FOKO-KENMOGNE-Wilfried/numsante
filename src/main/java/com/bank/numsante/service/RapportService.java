package com.bank.numsante.service;

import com.bank.numsante.dto.DashboardStats;
import com.bank.numsante.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RapportService {

    private final PatientRepository patientRepo;
    private final HopitalRepository hopitalRepo;
    private final PersonnelMedicalRepository personnelRepo;
    private final PassageMedicalRepository passageRepo;
    private final ExamenLaboratoireRepository examenRepo;
    private final PrescriptionMedicamentRepository prescriptionRepo;

    public DashboardStats getDashboardStats() {
        LocalDateTime debutJournee = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        return DashboardStats.builder()
                .totalPatients(patientRepo.count())
                .totalHopitaux(hopitalRepo.count())
                .totalPersonnel(personnelRepo.count())
                .totalPassagesAujourdhui(passageRepo.countByDateAdmissionAfter(debutJournee))
                .totalPassagesEnCours(passageRepo.countByStatutPassage("en_cours"))
                .totalExamensRealises(examenRepo.count())
                .totalOrdonnances(prescriptionRepo.countByDelivre(true))
                .build();
    }
}