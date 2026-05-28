package com.bank.numsante.repository;

import com.bank.numsante.entity.PassageMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PassageMedicalRepository extends JpaRepository<PassageMedical, UUID> {
    List<PassageMedical> findByPatient_IdPatientOrderByDateAdmissionDesc(UUID idPatient);
}