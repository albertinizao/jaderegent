package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.Ventaja;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentajaRepository extends JpaRepository<Ventaja, String> {
    List<Ventaja> findByNpcNpcId(String npcId);
}
