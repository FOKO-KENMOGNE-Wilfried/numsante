package com.bank.numsante.service;

import com.bank.numsante.dto.ConstantesVitalesRequest;
import com.bank.numsante.dto.ConsultationRequest;
import com.bank.numsante.dto.PageResponse;
import com.bank.numsante.entity.PassageMedical;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.repository.PassageMedicalRepository;
import com.bank.numsante.repository.PersonnelMedicalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassageService {

    private final PassageMedicalRepository passageRepo;
    private final PersonnelMedicalRepository personnelRepo;
    private final LogService logService;

    public PageResponse<PassageMedical> getPassagesByHopital(Long idHopital, int page, int size) {
        Page<PassageMedical> passagesPage = passageRepo.findByHopital_IdHopital(idHopital, PageRequest.of(page, size));

        return PageResponse.<PassageMedical>builder()
                .content(passagesPage.getContent())
                .page(passagesPage.getNumber())
                .size(passagesPage.getSize())
                .totalElements(passagesPage.getTotalElements())
                .totalPages(passagesPage.getTotalPages())
                .last(passagesPage.isLast())
                .first(passagesPage.isFirst())
                .build();
    }

    public List<PassageMedical> getPassagesEnCours(Long idHopital) {
        return passageRepo.findByHopital_IdHopitalAndStatutPassage(idHopital, "EN_COURS");
    }

    @Transactional
    public void annulerPassage(UUID idPassage, String username) {
        PassageMedical passage = passageRepo.findById(idPassage)
                .orElseThrow(() -> new RuntimeException("Passage introuvable"));
        passage.setStatutPassage("annule");
        passageRepo.save(passage);

        PersonnelMedical personnel = personnelRepo.findByIdentifiantPro(username).orElse(null);
        logService.logAction(personnel != null ? personnel.getIdPersonnel() : null,
                passage.getPatient().getIdPatient(),
                "ANNULATION_PASSAGE", passage.getIdPassage());
    }

    @Transactional
    public void updateConstantes(UUID idPassage, ConstantesVitalesRequest request, String username) {
        PassageMedical passage = passageRepo.findById(idPassage)
                .orElseThrow(() -> new RuntimeException("Passage introuvable"));
        passage.setConstantesVitales(request.getConstantesVitales());
        passageRepo.save(passage);

        PersonnelMedical personnel = personnelRepo.findByIdentifiantPro(username).orElse(null);
        logService.logAction(personnel != null ? personnel.getIdPersonnel() : null,
                passage.getPatient().getIdPatient(),
                "MAJ_CONSTANTES", passage.getIdPassage());
    }

    @Transactional
    public void ajouterConsultation(UUID idPassage, ConsultationRequest request, String username) {
        PassageMedical passage = passageRepo.findById(idPassage)
                .orElseThrow(() -> new RuntimeException("Passage introuvable"));
        if (request.getDiagnostic() != null) passage.setDiagnostic(request.getDiagnostic());
        if (request.getPrescriptionOrdonnance() != null)
            passage.setPrescriptionOrdonnance(request.getPrescriptionOrdonnance());
        if (request.isCloturerPassage()) passage.setStatutPassage("termine");
        passageRepo.save(passage);

        PersonnelMedical personnel = personnelRepo.findByIdentifiantPro(username).orElse(null);
        logService.logAction(personnel != null ? personnel.getIdPersonnel() : null,
                passage.getPatient().getIdPatient(),
                "CONSULTATION", passage.getIdPassage());
    }
}