package com.bank.numsante.repository;

import com.bank.numsante.entity.ExamenLaboratoire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamenLaboratoireRepository extends JpaRepository<ExamenLaboratoire, Long> {

    List<ExamenLaboratoire> findByPassage_IdPassage(UUID idPassage);
}