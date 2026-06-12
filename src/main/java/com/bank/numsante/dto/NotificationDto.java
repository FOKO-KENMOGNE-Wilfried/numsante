package com.bank.numsante.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long idNotification;
    private String type;
    private String titre;
    private String message;
    private Boolean estLu;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
    private UUID idPassage;
    private String donnees;
}
