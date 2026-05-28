package com.bank.numsante.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logs_tracabilite")
@Data @NoArgsConstructor @AllArgsConstructor
public class LogTracabilite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLog;

    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    @Column(name = "id_patient")
    private UUID idPatient;

    @Column(nullable = false)
    private String actionEffectuee;

    @Column(name = "id_dossier_concerne")
    private UUID idDossierConcerne;

    private String adresseIp;

    @CreationTimestamp
    private LocalDateTime horodatage;
}