package com.bank.numsante.service;

import com.bank.numsante.dto.ExamenRequest;
import com.bank.numsante.entity.ExamenLaboratoire;
import com.bank.numsante.entity.PassageMedical;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.repository.ExamenLaboratoireRepository;
import com.bank.numsante.repository.PassageMedicalRepository;
import com.bank.numsante.repository.PersonnelMedicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LaboratoireService {

    private final ExamenLaboratoireRepository examenRepo;
    private final PassageMedicalRepository passageRepo;
    private final PersonnelMedicalRepository personnelRepo;
    private final LogService logService;

    public void ajouterExamen(ExamenRequest request, String username) {
        PassageMedical passage = passageRepo.findById(UUID.fromString(request.getIdPassage()))
                .orElseThrow(() -> new RuntimeException("Passage introuvable"));
        PersonnelMedical laborantin = personnelRepo.findByIdentifiantPro(username)
                .orElseThrow(() -> new RuntimeException("Laborantin non trouvé"));

        ExamenLaboratoire examen = new ExamenLaboratoire();
        examen.setPassage(passage);
        examen.setLaborantin(laborantin);
        examen.setTypeExamen(request.getTypeExamen());
        examen.setResultats(request.getResultats());
        examenRepo.save(examen);

        logService.logAction(laborantin.getIdPersonnel(),
                passage.getPatient().getIdPatient(),
                "AJOUT_EXAMEN", passage.getIdPassage());
    }

    public List<ExamenLaboratoire> getExamensByPassage(UUID idPassage) {
        return examenRepo.findByPassage_IdPassage(idPassage);
    }
}