package com.bank.numsante.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(nullable = false, length = 100)
    private String type; // NOUVEAU_PASSAGE, CONSTANTES_PRISES, CONSULTATION_TERMINEE, PRESCRIPTION_CREEE, PRESCRIPTION_DELIVREE, RESULTATS_LABO, CARTE_SUSPENDUE, CARTE_RENOUVELEE

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "est_lu")
    private Boolean estLu = false;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    // Référence optionnelle au passage médical concerné
    @Column(name = "id_passage")
    private UUID idPassage;

    // Données supplémentaires au format JSON (optionnel)
    @Column(columnDefinition = "TEXT")
    private String donnees;
}
