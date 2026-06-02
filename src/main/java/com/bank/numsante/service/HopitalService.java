package com.bank.numsante.service;

import com.bank.numsante.dto.CreateHopitalRequest;
import com.bank.numsante.entity.Hopital;
import com.bank.numsante.repository.HopitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HopitalService {

    private final HopitalRepository hopitalRepository;
    private final LogService logService;

    @Transactional
    public Hopital creerHopital(CreateHopitalRequest request, String adminUsername) {
        if (hopitalRepository.existsByCodeUnique(request.getCodeUnique())) {
            throw new RuntimeException("Un hôpital avec ce code unique existe déjà");
        }

        Hopital hopital = new Hopital();
        hopital.setNom(request.getNom());
        hopital.setAdresse(request.getAdresse());
        hopital.setCodeUnique(request.getCodeUnique());

        Hopital saved = hopitalRepository.save(hopital);
        logService.logAction(null, null, "CREATION_HOPITAL_" + saved.getCodeUnique(), null);
        return saved;
    }

    public List<Hopital> getAllHopitaux() {
        return hopitalRepository.findAll();
    }

    public Hopital getHopitalById(Long id) {
        return hopitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé"));
    }

    @Transactional
    public Hopital updateHopital(Long id, CreateHopitalRequest request) {
        Hopital hopital = getHopitalById(id);
        hopital.setNom(request.getNom());
        hopital.setAdresse(request.getAdresse());
        hopital.setCodeUnique(request.getCodeUnique());
        return hopitalRepository.save(hopital);
    }

    @Transactional
    public void deleteHopital(Long id) {
        if (!hopitalRepository.existsById(id)) {
            throw new RuntimeException("Hôpital non trouvé");
        }
        hopitalRepository.deleteById(id);
    }
}