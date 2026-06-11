package com.bank.numsante.config;

import com.bank.numsante.entity.*;
import com.bank.numsante.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final HopitalRepository hopitalRepo;
    private final PersonnelMedicalRepository personnelRepo;
    private final PatientRepository patientRepo;
    private final CarteNumeriqueRepository carteRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (hopitalRepo.count() == 0) {
            // Créer 2 hôpitaux
            Hopital h1 = new Hopital();
            h1.setNom("Hôpital Général de Yaoundé");
            h1.setAdresse("Yaoundé, Centre");
            h1.setCodeUnique("HGY-CMR");
            hopitalRepo.save(h1);

            Hopital h2 = new Hopital();
            h2.setNom("Hôpital Central de Douala");
            h2.setAdresse("Douala, Littoral");
            h2.setCodeUnique("HCD-CMR");
            hopitalRepo.save(h2);

            // Créer personnel pour chaque rôle
            String encodedPassword = encoder.encode("passer123");

            // Admin
            creerPersonnel(personnelRepo, h1, "Admin", "Système", "admin", "admin_sys", encodedPassword);

            // Médecins
            creerPersonnel(personnelRepo, h1, "Mballa", "Jean", "medecin", "dr_mballa", encodedPassword);
            creerPersonnel(personnelRepo, h2, "Kamga", "Marie", "medecin", "dr_kamga", encodedPassword);

            // Infirmiers
            creerPersonnel(personnelRepo, h1, "Ngo", "Sophie", "infirmier", "inf_ngo", encodedPassword);
            creerPersonnel(personnelRepo, h2, "Tchinda", "Paul", "infirmier", "inf_tchinda", encodedPassword);

            // Accueil
            creerPersonnel(personnelRepo, h1, "Eyanga", "Christine", "accueil", "acc_eyanga", encodedPassword);
            creerPersonnel(personnelRepo, h2, "Mbarga", "David", "accueil", "acc_mbarga", encodedPassword);

            // Laborantins
            creerPersonnel(personnelRepo, h1, "Ekobo", "Alain", "laborantin", "lab_ekobo", encodedPassword);
            creerPersonnel(personnelRepo, h2, "Belinga", "Esther", "laborantin", "lab_belinga", encodedPassword);

            // Pharmaciens
            creerPersonnel(personnelRepo, h1, "Owona", "Pierre", "pharmacien", "ph_owona", encodedPassword);
            creerPersonnel(personnelRepo, h2, "Atangana", "Lucie", "pharmacien", "ph_atangana", encodedPassword);

            System.out.println("✅ Données de test initialisées avec succès !");
            System.out.println("📋 Comptes disponibles :");
            System.out.println("   admin_sys / passer123 (ADMIN)");
            System.out.println("   dr_mballa / passer123 (MEDECIN)");
            System.out.println("   inf_ngo / passer123 (INFIRMIER)");
            System.out.println("   acc_eyanga / passer123 (ACCUEIL)");
            System.out.println("   lab_ekobo / passer123 (LABORANTIN)");
            System.out.println("   ph_owona / passer123 (PHARMACIEN)");
        }
    }

    private void creerPersonnel(PersonnelMedicalRepository repo, Hopital hopital,
                                String nom, String prenom, String role,
                                String identifiant, String password) {
        PersonnelMedical p = new PersonnelMedical();
        p.setHopital(hopital);
        p.setNom(nom);
        p.setPrenom(prenom);
        p.setRole(role);
        p.setIdentifiantPro(identifiant);
        p.setMotDePasseHash(password);
        p.setEstActif(true);
        repo.save(p);
    }

}