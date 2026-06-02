package com.bank.numsante.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics
                        .requestMatchers(
                                "/auth/login-professionnel",
                                "/auth/login-biometrique",
                                "/auth/enregistrer-biometrie",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/actuator/health"
                        ).permitAll()

                        // ADMIN - Gestion complète du personnel et hôpitaux
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hopitaux/**").hasRole("ADMIN")

                        // ADMIN - CRUD personnel
                        .requestMatchers(HttpMethod.POST, "/personnel/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/personnel/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/personnel/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/personnel/**").hasAnyRole("ADMIN", "MEDECIN")

                        // ACCUEIL - Scan QR et création admission
                        .requestMatchers("/admission/scan-carte").hasAnyRole("ACCUEIL", "MEDECIN", "INFIRMIER")
                        .requestMatchers("/admission/creer-passage").hasRole("ACCUEIL")

                        // MEDECIN & INFIRMIER - Constantes vitales
                        .requestMatchers(HttpMethod.PUT, "/passages/*/constantes").hasAnyRole("INFIRMIER", "MEDECIN")

                        // MEDECIN uniquement - Consultation
                        .requestMatchers(HttpMethod.PUT, "/passages/*/consultation").hasRole("MEDECIN")

                        // LABORANTIN - Examens
                        .requestMatchers("/laboratoire/**").hasRole("LABORANTIN")

                        // PHARMACIEN - Validation prescriptions
                        .requestMatchers("/pharmacie/**").hasRole("PHARMACIEN")

                        // Lecture historique - plusieurs rôles
                        .requestMatchers(HttpMethod.GET, "/patients/*/historique").hasAnyRole("MEDECIN", "INFIRMIER", "PATIENT")

                        // Rapports accessibles aux admins et medecins
                        .requestMatchers("/rapports/**").hasAnyRole("ADMIN", "MEDECIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}