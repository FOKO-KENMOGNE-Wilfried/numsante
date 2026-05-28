package com.bank.numsante.repository;

import com.bank.numsante.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByNomIgnoreCaseAndPrenomIgnoreCase(String nom, String prenom);
}