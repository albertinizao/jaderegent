package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeleccionVentajaRepository extends JpaRepository<SeleccionVentaja, UUID> {
    Optional<SeleccionVentaja> findByRelacionRelacionIdAndNivelAdquisicion(UUID relacionId, Integer nivelAdquisicion);

    List<SeleccionVentaja> findByRelacionRelacionId(UUID relacionId);
}
