package com.bank.numsante.repository;

import com.bank.numsante.entity.PrescriptionMedicament;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PrescriptionMedicamentRepository extends JpaRepository<PrescriptionMedicament, Long> {
    List<PrescriptionMedicament> findByPassage_IdPassage(UUID idPassage);
    long countByDelivre(boolean delivre);
    List<PrescriptionMedicament> findByDelivreFalse();
}