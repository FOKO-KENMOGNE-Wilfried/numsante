package com.bank.numsante.service;

import com.bank.numsante.dto.NotificationDto;
import com.bank.numsante.entity.Notification;
import com.bank.numsante.entity.Patient;
import com.bank.numsante.repository.NotificationRepository;
import com.bank.numsante.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PatientRepository patientRepository;

    /**
     * Créer une nouvelle notification pour un patient
     */
    @Transactional
    public NotificationDto creerNotification(UUID idPatient, String type, String titre, String message, UUID idPassage, String donnees) {
        Patient patient = patientRepository.findById(idPatient)
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));

        Notification notification = new Notification();
        notification.setPatient(patient);
        notification.setType(type);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setEstLu(false);
        notification.setIdPassage(idPassage);
        notification.setDonnees(donnees);

        Notification saved = notificationRepository.save(notification);

        System.out.println("📬 Notification créée: " + type + " pour patient " + idPatient);

        return convertToDto(saved);
    }

    /**
     * Récupérer toutes les notifications d'un patient (paginées)
     */
    public Page<NotificationDto> getNotificationsPatient(UUID idPatient, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository
                .findByPatient_IdPatientOrderByDateCreationDesc(idPatient, pageable);
        return notifications.map(this::convertToDto);
    }

    /**
     * Récupérer les notifications non lues d'un patient
     */
    public List<NotificationDto> getNotificationsNonLues(UUID idPatient) {
        List<Notification> notifications = notificationRepository
                .findByPatient_IdPatientAndEstLuFalseOrderByDateCreationDesc(idPatient);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Compter les notifications non lues
     */
    public long compterNotificationsNonLues(UUID idPatient) {
        return notificationRepository.countByPatient_IdPatientAndEstLuFalse(idPatient);
    }

    /**
     * Marquer une notification comme lue
     */
    @Transactional
    public NotificationDto marquerCommeLue(Long idNotification) {
        Notification notification = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));

        if (!notification.getEstLu()) {
            notification.setEstLu(true);
            notification.setDateLecture(LocalDateTime.now());
            notification = notificationRepository.save(notification);
            System.out.println("✅ Notification " + idNotification + " marquée comme lue");
        }

        return convertToDto(notification);
    }

    /**
     * Marquer toutes les notifications d'un patient comme lues
     */
    @Transactional
    public int marquerToutesCommeLues(UUID idPatient) {
        List<Notification> notifications = notificationRepository
                .findByPatient_IdPatientAndEstLuFalseOrderByDateCreationDesc(idPatient);

        LocalDateTime maintenant = LocalDateTime.now();
        notifications.forEach(notif -> {
            notif.setEstLu(true);
            notif.setDateLecture(maintenant);
        });

        notificationRepository.saveAll(notifications);
        System.out.println("✅ " + notifications.size() + " notifications marquées comme lues pour patient " + idPatient);

        return notifications.size();
    }

    /**
     * Supprimer une notification
     */
    @Transactional
    public void supprimerNotification(Long idNotification) {
        notificationRepository.deleteById(idNotification);
        System.out.println("🗑️ Notification " + idNotification + " supprimée");
    }

    /**
     * Supprimer toutes les notifications lues d'un patient
     */
    @Transactional
    public int supprimerNotificationsLues(UUID idPatient) {
        List<Notification> notifications = notificationRepository
                .findByPatient_IdPatientOrderByDateCreationDesc(idPatient)
                .stream()
                .filter(Notification::getEstLu)
                .collect(Collectors.toList());

        notificationRepository.deleteAll(notifications);
        System.out.println("🗑️ " + notifications.size() + " notifications lues supprimées pour patient " + idPatient);

        return notifications.size();
    }

    // Méthodes utilitaires pour créer des notifications spécifiques

    public NotificationDto notifierNouveauPassage(UUID idPatient, UUID idPassage, String motif) {
        return creerNotification(
                idPatient,
                "NOUVEAU_PASSAGE",
                "Nouveau passage médical",
                "Votre passage médical pour \"" + motif + "\" a été enregistré.",
                idPassage,
                null
        );
    }

    public NotificationDto notifierConstantesPrises(UUID idPatient, UUID idPassage) {
        return creerNotification(
                idPatient,
                "CONSTANTES_PRISES",
                "Constantes prises",
                "Vos constantes vitales ont été enregistrées par l'infirmier(ère).",
                idPassage,
                null
        );
    }

    public NotificationDto notifierConsultationTerminee(UUID idPatient, UUID idPassage, String nomMedecin) {
        return creerNotification(
                idPatient,
                "CONSULTATION_TERMINEE",
                "Consultation terminée",
                "Votre consultation avec le Dr " + nomMedecin + " est terminée.",
                idPassage,
                null
        );
    }

    public NotificationDto notifierPrescriptionCreee(UUID idPatient, UUID idPassage) {
        return creerNotification(
                idPatient,
                "PRESCRIPTION_CREEE",
                "Nouvelle prescription",
                "Une ordonnance a été créée lors de votre consultation.",
                idPassage,
                null
        );
    }

    public NotificationDto notifierPrescriptionDelivree(UUID idPatient, UUID idPassage) {
        return creerNotification(
                idPatient,
                "PRESCRIPTION_DELIVREE",
                "Médicaments délivrés",
                "Vos médicaments ont été délivrés par la pharmacie.",
                idPassage,
                null
        );
    }

    public NotificationDto notifierResultatsLabo(UUID idPatient, UUID idPassage, String typeExamen) {
        return creerNotification(
                idPatient,
                "RESULTATS_LABO",
                "Résultats d'examen disponibles",
                "Les résultats de votre examen \"" + typeExamen + "\" sont disponibles.",
                idPassage,
                null
        );
    }

    public NotificationDto notifierCarteSuspendue(UUID idPatient, String motif) {
        return creerNotification(
                idPatient,
                "CARTE_SUSPENDUE",
                "Carte numérique suspendue",
                "Votre carte numérique a été suspendue. Motif: " + motif,
                null,
                null
        );
    }

    public NotificationDto notifierCarteRenouvelee(UUID idPatient) {
        return creerNotification(
                idPatient,
                "CARTE_RENOUVELEE",
                "Nouvelle carte numérique",
                "Votre carte numérique a été renouvelée avec succès. Un nouveau QR code est disponible.",
                null,
                null
        );
    }

    // Conversion DTO
    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setIdNotification(notification.getIdNotification());
        dto.setType(notification.getType());
        dto.setTitre(notification.getTitre());
        dto.setMessage(notification.getMessage());
        dto.setEstLu(notification.getEstLu());
        dto.setDateCreation(notification.getDateCreation());
        dto.setDateLecture(notification.getDateLecture());
        dto.setIdPassage(notification.getIdPassage());
        dto.setDonnees(notification.getDonnees());
        return dto;
    }
}
