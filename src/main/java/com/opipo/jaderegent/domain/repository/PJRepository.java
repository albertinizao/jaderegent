package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.PJ;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PJRepository extends JpaRepository<PJ, UUID> {
    boolean existsByNombreDisplay(String nombreDisplay);
    Optional<PJ> findByNombreDisplay(String nombreDisplay);
}
