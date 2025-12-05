package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.Relacion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelacionRepository extends JpaRepository<Relacion, UUID> {
    Optional<Relacion> findByPjPjIdAndNpcNpcId(UUID pjId, String npcId);
    List<Relacion> findByPjPjId(UUID pjId);
    List<Relacion> findByNpcNpcId(String npcId);
}
