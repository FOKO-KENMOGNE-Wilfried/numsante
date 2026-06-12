package com.bank.numsante.repository;

import com.bank.numsante.entity.LogTracabilite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LogTracabiliteRepository extends JpaRepository<LogTracabilite, Long> {

    Page<LogTracabilite> findByIdPatient(UUID idPatient, Pageable pageable);

    List<LogTracabilite> findByIdDossierConcerne(UUID idDossierConcerne);

    Page<LogTracabilite> findByIdUtilisateur(Long idUtilisateur, Pageable pageable);

    Page<LogTracabilite> findByActionEffectuee(String action, Pageable pageable);

    @Query("SELECT l FROM LogTracabilite l WHERE " +
           "(:action IS NULL OR l.actionEffectuee = :action) AND " +
           "(:idPatient IS NULL OR l.idPatient = :idPatient) AND " +
           "(:idUtilisateur IS NULL OR l.idUtilisateur = :idUtilisateur)")
    Page<LogTracabilite> searchLogs(
        @Param("action") String action,
        @Param("idPatient") UUID idPatient,
        @Param("idUtilisateur") Long idUtilisateur,
        Pageable pageable
    );
}