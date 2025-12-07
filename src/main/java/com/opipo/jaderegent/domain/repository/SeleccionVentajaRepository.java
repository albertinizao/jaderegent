package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeleccionVentajaRepository extends JpaRepository<SeleccionVentaja, UUID> {
    Optional<SeleccionVentaja> findByRelacionRelacionIdAndNivelAdquisicion(UUID relacionId, Integer nivelAdquisicion);

    List<SeleccionVentaja> findByRelacionRelacionId(UUID relacionId);

    @Modifying
    @Query("DELETE FROM SeleccionVentaja sv WHERE sv.relacion.relacionId = :relacionId")
    void deleteByRelacionRelacionId(@Param("relacionId") UUID relacionId);
}
