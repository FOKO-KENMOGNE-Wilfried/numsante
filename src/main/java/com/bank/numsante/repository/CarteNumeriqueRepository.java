package com.bank.numsante.repository;

import com.bank.numsante.entity.CarteNumerique;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarteNumeriqueRepository extends JpaRepository<CarteNumerique, Long> {
    Optional<CarteNumerique> findByQrCodeToken(String token);
}
