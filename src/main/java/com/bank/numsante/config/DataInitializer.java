package com.bank.numsante.config;

import com.bank.numsante.entity.Hopital;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.repository.HopitalRepository;
import com.bank.numsante.repository.PersonnelMedicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final HopitalRepository hopitalRepo;
    private final PersonnelMedicalRepository personnelRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (hopitalRepo.count() == 0) {
            Hopital h = new Hopital(null, "Hôpital Général", "Yaoundé", "HG-CMR", null);
            hopitalRepo.save(h);

            PersonnelMedical med = new PersonnelMedical();
            med.setHopital(h);
            med.setNom("Mballa");
            med.setPrenom("Jean");
            med.setRole("medecin");
            med.setIdentifiantPro("dr_mballa");
            med.setMotDePasseHash(encoder.encode("passer123"));
            med.setEstActif(true);
            personnelRepo.save(med);
        }
    }
}