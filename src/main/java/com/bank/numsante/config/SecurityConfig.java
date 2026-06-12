package com.bank.numsante.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers(
                                "/auth/login-professionnel",
                                "/auth/login-patient",
                                "/auth/register-patient",
                                "/auth/login-biometrique",
                                "/auth/enregistrer-biometrie",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/actuator/health"
                        ).permitAll()

                        // Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hopitaux/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/personnel/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/personnel/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/personnel/**").hasRole("ADMIN")

                        // Lecture personnel (médecins, admin)
                        .requestMatchers(HttpMethod.GET, "/personnel/**").hasAnyRole("ADMIN", "MEDECIN")

                        // Accueil
                        .requestMatchers("/admission/scan-carte").hasAnyRole("ACCUEIL", "MEDECIN", "INFIRMIER")
                        .requestMatchers("/admission/creer-passage").hasAnyRole("ACCUEIL", "MEDECIN", "INFIRMIER")

                        // Constantes
                        .requestMatchers(HttpMethod.PUT, "/passages/*/constantes").hasAnyRole("INFIRMIER", "MEDECIN")

                        // Consultation (médecin)
                        .requestMatchers(HttpMethod.PUT, "/passages/*/consultation").hasRole("MEDECIN")

                        // Laboratoire
                        .requestMatchers("/laboratoire/**").hasRole("LABORANTIN")

                        // Pharmacie
                        .requestMatchers("/pharmacie/**").hasRole("PHARMACIEN")

                        // Historique patient (lui-même, médecin, infirmier)
                        .requestMatchers(HttpMethod.GET, "/patients/*/historique").hasAnyRole("MEDECIN", "INFIRMIER", "PATIENT")

                        // Gestion patients (CRUD admin/medecin)
                        .requestMatchers(HttpMethod.POST, "/patients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/patients/**").hasRole("MEDECIN")
                        .requestMatchers(HttpMethod.GET, "/patients/**").hasAnyRole("ADMIN", "MEDECIN", "ACCUEIL", "INFIRMIER", "PHARMACIEN", "LABORANTIN", "PATIENT")

                        // Rapports
                        .requestMatchers("/rapports/**").hasAnyRole("ADMIN", "MEDECIN")

                        // Logs et traçabilité
                        .requestMatchers(HttpMethod.GET, "/logs").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/logs/patient/**").hasAnyRole("ADMIN", "MEDECIN")
                        .requestMatchers(HttpMethod.GET, "/logs/passage/**").hasAnyRole("ADMIN", "MEDECIN", "INFIRMIER")
                        .requestMatchers(HttpMethod.GET, "/logs/personnel/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/logs/search").hasRole("ADMIN")

                        // Notifications (patients peuvent consulter leurs propres notifications)
                        .requestMatchers(HttpMethod.GET, "/notifications/patient/**").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PUT, "/notifications/*/marquer-lue").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PUT, "/notifications/patient/*/marquer-toutes-lues").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.DELETE, "/notifications/**").hasRole("PATIENT")

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