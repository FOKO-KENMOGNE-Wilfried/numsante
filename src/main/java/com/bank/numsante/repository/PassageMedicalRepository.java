package com.bank.numsante.repository;

import com.bank.numsante.entity.PassageMedical;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PassageMedicalRepository extends JpaRepository<PassageMedical, UUID> {
    List<PassageMedical> findByPatient_IdPatientOrderByDateAdmissionDesc(UUID idPatient);

    Page<PassageMedical> findByPatient_IdPatientOrderByDateAdmissionDesc(UUID idPatient, Pageable pageable);

    Page<PassageMedical> findByHopital_IdHopital(Long idHopital, Pageable pageable);

    List<PassageMedical> findByHopital_IdHopitalAndStatutPassage(Long idHopital, String statut);

    @Query("SELECT p FROM PassageMedical p WHERE p.dateAdmission >= :debut AND p.dateAdmission <= :fin")
    List<PassageMedical> findByDateAdmissionBetween(LocalDateTime debut, LocalDateTime fin);

    long countByStatutPassage(String statut);

    long countByDateAdmissionAfter(LocalDateTime date);
}