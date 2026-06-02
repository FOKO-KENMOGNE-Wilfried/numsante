package com.bank.numsante.repository;

import com.bank.numsante.entity.Hopital;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HopitalRepository extends JpaRepository<Hopital, Long> {
    Optional<Hopital> findByCodeUnique(String codeUnique);
    boolean existsByCodeUnique(String codeUnique);
}