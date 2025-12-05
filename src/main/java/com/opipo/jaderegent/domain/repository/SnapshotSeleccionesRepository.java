package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.SnapshotSelecciones;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotSeleccionesRepository extends JpaRepository<SnapshotSelecciones, UUID> {
    List<SnapshotSelecciones> findByRelacionRelacionIdOrderByVersionDesc(UUID relacionId);
}
