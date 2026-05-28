package com.bank.numsante.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "passages_medicaux")
@Data @NoArgsConstructor @AllArgsConstructor
public class PassageMedical {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_passage", updatable = false, nullable = false)
    private UUID idPassage;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "id_hopital", nullable = false)
    private Hopital hopital;

    @ManyToOne
    @JoinColumn(name = "id_createur", nullable = false)
    private PersonnelMedical createur;

    @CreationTimestamp
    @Column(name = "date_admission")
    private LocalDateTime dateAdmission;

    @Column(name = "motif_visite", nullable = false)
    private String motifVisite;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "constantes_vitales", columnDefinition = "jsonb")
    private Map<String, Object> constantesVitales;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    @Column(columnDefinition = "TEXT")
    private String prescriptionOrdonnance;

    @Column(nullable = false)
    private String statutPassage = "en_cours"; // en_cours, termine
}