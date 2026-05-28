package com.bank.numsante.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personnel_medical")
@Data @NoArgsConstructor @AllArgsConstructor
public class PersonnelMedical {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersonnel;

    @ManyToOne
    @JoinColumn(name = "id_hopital")
    private Hopital hopital;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String role; // medecin, infirmier, accueil, laborantin, pharmacien, admin

    @Column(unique = true, nullable = false)
    private String identifiantPro;

    @Column(nullable = false)
    private String motDePasseHash;

    @Column(columnDefinition = "TEXT")
    private String clePubliqueAppareil;

    private Boolean estActif = true;
}