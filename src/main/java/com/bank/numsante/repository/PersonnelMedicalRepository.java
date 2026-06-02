package com.bank.numsante.repository;

import com.bank.numsante.entity.PersonnelMedical;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PersonnelMedicalRepository extends JpaRepository<PersonnelMedical, Long> {
    Optional<PersonnelMedical> findByIdentifiantPro(String identifiantPro);

    List<PersonnelMedical> findByRole(String role);

    List<PersonnelMedical> findByHopital_IdHopital(Long idHopital);

    Page<PersonnelMedical> findByRoleContainingIgnoreCase(String role, Pageable pageable);

    @Query("SELECT p FROM PersonnelMedical p WHERE p.estActif = true")
    List<PersonnelMedical> findAllActif();

    long countByRole(String role);
}