package com.bank.numsante.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Data @NoArgsConstructor @AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_patient", updatable = false, nullable = false)
    private UUID idPatient;

    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    private Character genre;
    private String groupeSanguin;
    private String telephone;

    @Column(name = "cle_publique_biometrique", columnDefinition = "TEXT")
    private String clePubliqueBiometrique;

    @CreationTimestamp
    @Column(name = "cree_le")
    private LocalDateTime creeLe;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private CarteNumerique carteNumerique;

    @OneToMany(mappedBy = "patient")
    private List<PassageMedical> passages;
}