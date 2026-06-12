package com.bank.numsante.service;

import com.bank.numsante.entity.LogTracabilite;
import com.bank.numsante.repository.LogTracabiliteRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Page<LogTracabilite> getAllLogs(int page, int size, String action) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("horodatage").descending());
        if (action != null && !action.isEmpty()) {
            return logRepo.findByActionEffectuee(action, pageable);
        }
        return logRepo.findAll(pageable);
    }

    public Page<LogTracabilite> getLogsByPatient(UUID idPatient, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("horodatage").descending());
        return logRepo.findByIdPatient(idPatient, pageable);
    }

    public List<LogTracabilite> getLogsByPassage(UUID idPassage) {
        return logRepo.findByIdDossierConcerne(idPassage);
    }

    public Page<LogTracabilite> getLogsByPersonnel(Long idPersonnel, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("horodatage").descending());
        return logRepo.findByIdUtilisateur(idPersonnel, pageable);
    }

    public Page<LogTracabilite> searchLogs(String action, UUID idPatient, Long idUtilisateur, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("horodatage").descending());
        return logRepo.searchLogs(action, idPatient, idUtilisateur, pageable);
    }
}