package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.Interaccion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteraccionRepository extends JpaRepository<Interaccion, UUID> {
    List<Interaccion> findByRelacionRelacionIdOrderByTsDesc(UUID relacionId);
}
