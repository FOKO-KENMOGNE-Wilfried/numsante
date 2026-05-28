package com.bank.numsante.repository;

import com.bank.numsante.entity.PersonnelMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonnelMedicalRepository extends JpaRepository<PersonnelMedical, Long> {
    Optional<PersonnelMedical> findByIdentifiantPro(String identifiantPro);
}