package com.bank.numsante.service;

import com.bank.numsante.dto.CreatePersonnelRequest;
import com.bank.numsante.dto.PageResponse;
import com.bank.numsante.dto.ResetPasswordRequest;
import com.bank.numsante.dto.UpdatePersonnelRequest;
import com.bank.numsante.entity.Hopital;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.repository.HopitalRepository;
import com.bank.numsante.repository.PersonnelMedicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonnelService {

    private final PersonnelMedicalRepository personnelRepo;
    private final HopitalRepository hopitalRepo;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Transactional
    public PersonnelMedical creerPersonnel(CreatePersonnelRequest request, String adminUsername) {
        if (personnelRepo.findByIdentifiantPro(request.getIdentifiantPro()).isPresent()) {
            throw new RuntimeException("Cet identifiant professionnel existe déjà");
        }

        Hopital hopital = hopitalRepo.findById(request.getIdHopital())
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé"));

        PersonnelMedical personnel = new PersonnelMedical();
        personnel.setNom(request.getNom());
        personnel.setPrenom(request.getPrenom());
        personnel.setRole(request.getRole().toLowerCase());
        personnel.setIdentifiantPro(request.getIdentifiantPro());
        personnel.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));
        personnel.setHopital(hopital);
        personnel.setEstActif(true);

        PersonnelMedical saved = personnelRepo.save(personnel);
        logService.logAction(null, null, "CREATION_PERSONNEL_" + saved.getRole().toUpperCase(), null);
        return saved;
    }

    public PageResponse<PersonnelMedical> getAllPersonnel(int page, int size, String role) {
        Page<PersonnelMedical> personnelPage;
        if (role != null && !role.isEmpty()) {
            personnelPage = personnelRepo.findByRoleContainingIgnoreCase(role, PageRequest.of(page, size));
        } else {
            personnelPage = personnelRepo.findAll(PageRequest.of(page, size));
        }

        return PageResponse.<PersonnelMedical>builder()
                .content(personnelPage.getContent())
                .page(personnelPage.getNumber())
                .size(personnelPage.getSize())
                .totalElements(personnelPage.getTotalElements())
                .totalPages(personnelPage.getTotalPages())
                .last(personnelPage.isLast())
                .first(personnelPage.isFirst())
                .build();
    }

    public List<PersonnelMedical> getPersonnelByRole(String role) {
        return personnelRepo.findByRole(role.toLowerCase());
    }

    public List<PersonnelMedical> getPersonnelByHopital(Long idHopital) {
        return personnelRepo.findByHopital_IdHopital(idHopital);
    }

    @Transactional
    public PersonnelMedical updatePersonnel(Long id, UpdatePersonnelRequest request) {
        PersonnelMedical personnel = personnelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        if (request.getNom() != null) personnel.setNom(request.getNom());
        if (request.getPrenom() != null) personnel.setPrenom(request.getPrenom());
        if (request.getRole() != null) personnel.setRole(request.getRole().toLowerCase());
        if (request.getEstActif() != null) personnel.setEstActif(request.getEstActif());
        if (request.getIdHopital() != null) {
            Hopital hopital = hopitalRepo.findById(request.getIdHopital())
                    .orElseThrow(() -> new RuntimeException("Hôpital non trouvé"));
            personnel.setHopital(hopital);
        }

        return personnelRepo.save(personnel);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PersonnelMedical personnel = personnelRepo.findByIdentifiantPro(request.getIdentifiantPro())
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        personnel.setMotDePasseHash(passwordEncoder.encode(request.getNouveauMotDePasse()));
        personnelRepo.save(personnel);
        logService.logAction(null, null, "RESET_PASSWORD_" + request.getIdentifiantPro(), null);
    }

    @Transactional
    public void deletePersonnel(Long id) {
        PersonnelMedical personnel = personnelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        personnel.setEstActif(false);
        personnelRepo.save(personnel);
        logService.logAction(null, null, "DESACTIVATION_PERSONNEL_" + id, null);
    }

    public List<PersonnelMedical> searchPersonnel(String query) {
        return personnelRepo.search(query);
    }
}