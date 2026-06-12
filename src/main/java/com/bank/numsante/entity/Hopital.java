package com.bank.numsante.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hopitaux")
@Data @NoArgsConstructor @AllArgsConstructor
public class Hopital {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHopital;

    @Column(nullable = false)
    private String nom;
    private String adresse;

    @Column(unique = true, nullable = false)
    private String codeUnique;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "hopital")
    @JsonIgnore
    private List<PersonnelMedical> personnels;
}