package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.LogAccion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogAccionRepository extends JpaRepository<LogAccion, UUID> {
    List<LogAccion> findByUsuarioOrderByTsDesc(String usuario);
}
