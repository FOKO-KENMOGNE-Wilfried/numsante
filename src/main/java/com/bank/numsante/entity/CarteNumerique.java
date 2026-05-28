package com.bank.numsante.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "cartes_numeriques")
@Data @NoArgsConstructor @AllArgsConstructor
public class CarteNumerique {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarte;

    @OneToOne
    @JoinColumn(name = "id_patient", unique = true, nullable = false)
    private Patient patient;

    @Column(name = "qr_code_token", unique = true, nullable = false)
    private String qrCodeToken;

    @Column(nullable = false)
    private String statut = "actif"; // actif, suspendu, perdu

    @Column(name = "expire_le", nullable = false)
    private LocalDate expireLe;
}