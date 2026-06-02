package com.bank.numsante.repository;

import com.bank.numsante.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByNomIgnoreCaseAndPrenomIgnoreCase(String nom, String prenom);

    Page<Patient> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom, String prenom, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.dateNaissance BETWEEN :debut AND :fin")
    Page<Patient> findByDateNaissanceBetween(LocalDate debut, LocalDate fin, Pageable pageable);

    long count();

    List<Patient> findByNomContainingIgnoreCase(String nom);
}