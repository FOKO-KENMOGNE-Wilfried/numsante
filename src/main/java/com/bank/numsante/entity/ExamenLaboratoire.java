package com.bank.numsante.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "examens_laboratoire")
@Data @NoArgsConstructor @AllArgsConstructor
public class ExamenLaboratoire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExamen;

    @ManyToOne
    @JoinColumn(name = "id_passage", nullable = false)
    private PassageMedical passage;

    @ManyToOne
    @JoinColumn(name = "id_laborantin", nullable = false)
    private PersonnelMedical laborantin;

    @Column(nullable = false)
    private String typeExamen;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resultats;

    @CreationTimestamp
    private LocalDateTime dateResultat;
}