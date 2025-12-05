package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.NPC;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NPCRepository extends JpaRepository<NPC, String> {
    boolean existsByNombre(String nombre);
    Optional<NPC> findByNombre(String nombre);
}
