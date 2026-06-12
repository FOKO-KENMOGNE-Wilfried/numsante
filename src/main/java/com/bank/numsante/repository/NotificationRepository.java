package com.bank.numsante.repository;

import com.bank.numsante.entity.Notification;
import com.bank.numsante.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Récupérer toutes les notifications d'un patient (paginées)
    Page<Notification> findByPatient_IdPatientOrderByDateCreationDesc(UUID idPatient, Pageable pageable);

    // Récupérer les notifications non lues d'un patient
    List<Notification> findByPatient_IdPatientAndEstLuFalseOrderByDateCreationDesc(UUID idPatient);

    // Compter les notifications non lues
    long countByPatient_IdPatientAndEstLuFalse(UUID idPatient);

    // Récupérer les notifications par type
    List<Notification> findByPatient_IdPatientAndTypeOrderByDateCreationDesc(UUID idPatient, String type);

    // Récupérer toutes les notifications d'un patient (non paginées)
    List<Notification> findByPatient_IdPatientOrderByDateCreationDesc(UUID idPatient);
}
