package com.bank.numsante.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Carnet de Santé Numérique API")
                        .version("1.0.0")
                        .description("""
                            API REST pour le système de carnet de santé numérique.

                            ## Fonctionnalités principales

                            ### Authentification
                            - Login classique (email/mot de passe) pour patients et professionnels
                            - Login biométrique (empreinte digitale, Face ID)
                            - Enregistrement de la biométrie
                            - JWT avec rôles (ROLE_PATIENT, ROLE_MEDECIN, etc.)

                            ### Patients
                            - Gestion du profil patient
                            - Historique des passages médicaux
                            - Carte numérique avec QR Code

                            ### Personnel Médical
                            - Médecins, infirmiers, personnel d'accueil
                            - Gestion des admissions et passages
                            - Saisie des constantes et consultations

                            ## Authentification

                            Utilisez le endpoint `/auth/login-patient` ou `/auth/login-professionnel` pour obtenir un JWT.
                            Ensuite, ajoutez le token dans l'en-tête : `Authorization: Bearer <token>`

                            ## URL de base

                            - Local: http://localhost:8082/api/v1
                            """)
                        .contact(new Contact()
                                .name("Équipe NumSanté")
                                .email("support@numsante.com"))
                )
                .addServersItem(new Server()
                        .url("http://localhost:" + serverPort + "/api/v1")
                        .description("Serveur de développement local"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("BearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT obtenu via /auth/login-patient ou /auth/login-professionnel")));
    }
}