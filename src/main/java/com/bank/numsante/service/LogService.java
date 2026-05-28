package com.bank.numsante.service;

import com.bank.numsante.entity.LogTracabilite;
import com.bank.numsante.repository.LogTracabiliteRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogTracabiliteRepository logRepo;
    private final HttpServletRequest httpRequest;

    public void logAction(Long idUtilisateur, UUID idPatient, String action, UUID idDossier) {
        LogTracabilite log = new LogTracabilite();
        log.setIdUtilisateur(idUtilisateur);
        log.setIdPatient(idPatient);
        log.setActionEffectuee(action);
        log.setIdDossierConcerne(idDossier);
        log.setAdresseIp(httpRequest.getRemoteAddr());
        logRepo.save(log);
    }
}