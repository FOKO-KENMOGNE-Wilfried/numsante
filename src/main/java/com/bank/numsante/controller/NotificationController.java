package com.bank.numsante.controller;

import com.bank.numsante.dto.NotificationDto;
import com.bank.numsante.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Notifications", description = "Gestion des notifications patients")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
        summary = "Récupérer les notifications d'un patient (paginées)",
        description = "Retourne toutes les notifications d'un patient avec pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des notifications récupérée"),
        @ApiResponse(responseCode = "404", description = "Patient introuvable")
    })
    @GetMapping("/patient/{idPatient}")
    public ResponseEntity<Page<NotificationDto>> getNotificationsPatient(
            @PathVariable UUID idPatient,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getNotificationsPatient(idPatient, page, size));
    }

    @Operation(
        summary = "Récupérer les notifications non lues",
        description = "Retourne uniquement les notifications non lues d'un patient"
    )
    @GetMapping("/patient/{idPatient}/non-lues")
    public ResponseEntity<List<NotificationDto>> getNotificationsNonLues(@PathVariable UUID idPatient) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(idPatient));
    }

    @Operation(
        summary = "Compter les notifications non lues",
        description = "Retourne le nombre de notifications non lues pour afficher un badge"
    )
    @GetMapping("/patient/{idPatient}/count-non-lues")
    public ResponseEntity<Map<String, Long>> compterNotificationsNonLues(@PathVariable UUID idPatient) {
        long count = notificationService.compterNotificationsNonLues(idPatient);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @Operation(
        summary = "Marquer une notification comme lue",
        description = "Change le statut d'une notification à 'lue' et enregistre la date de lecture"
    )
    @PutMapping("/{idNotification}/marquer-lue")
    public ResponseEntity<NotificationDto> marquerCommeLue(@PathVariable Long idNotification) {
        return ResponseEntity.ok(notificationService.marquerCommeLue(idNotification));
    }

    @Operation(
        summary = "Marquer toutes les notifications comme lues",
        description = "Marque toutes les notifications non lues d'un patient comme lues"
    )
    @PutMapping("/patient/{idPatient}/marquer-toutes-lues")
    public ResponseEntity<Map<String, Object>> marquerToutesCommeLues(@PathVariable UUID idPatient) {
        int count = notificationService.marquerToutesCommeLues(idPatient);
        return ResponseEntity.ok(Map.of(
            "statut", "success",
            "message", count + " notification(s) marquée(s) comme lue(s)"
        ));
    }

    @Operation(
        summary = "Supprimer une notification",
        description = "Supprime définitivement une notification"
    )
    @DeleteMapping("/{idNotification}")
    public ResponseEntity<Map<String, String>> supprimerNotification(@PathVariable Long idNotification) {
        notificationService.supprimerNotification(idNotification);
        return ResponseEntity.ok(Map.of(
            "statut", "success",
            "message", "Notification supprimée"
        ));
    }

    @Operation(
        summary = "Supprimer toutes les notifications lues",
        description = "Supprime toutes les notifications déjà lues d'un patient pour nettoyer"
    )
    @DeleteMapping("/patient/{idPatient}/supprimer-lues")
    public ResponseEntity<Map<String, Object>> supprimerNotificationsLues(@PathVariable UUID idPatient) {
        int count = notificationService.supprimerNotificationsLues(idPatient);
        return ResponseEntity.ok(Map.of(
            "statut", "success",
            "message", count + " notification(s) supprimée(s)"
        ));
    }
}
