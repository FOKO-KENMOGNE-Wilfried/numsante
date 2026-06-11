package com.bank.numsante.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions_medicaments")
@Data @NoArgsConstructor @AllArgsConstructor
public class PrescriptionMedicament {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_passage", nullable = false)
    @JsonIgnoreProperties({"prescriptions", "patient", "createur", "hopital"})
    private PassageMedical passage;

    @ManyToOne
    @JoinColumn(name = "id_pharmacien")
    @JsonIgnoreProperties({"hopital", "motDePasseHash"})
    private PersonnelMedical pharmacien;

    @Column(nullable = false)
    private String medicament;

    private String posologie;
    private String duree;
    private boolean delivre = false;

    @CreationTimestamp
    private LocalDateTime dateValidation;

    @Column(columnDefinition = "TEXT")
    private String commentaire;
}