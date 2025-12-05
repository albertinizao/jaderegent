package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.VentajaRepository;
import com.opipo.jaderegent.infrastructure.web.dto.NpcDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetNpcDetailUseCase {

    private final NPCRepository npcRepository;
    private final VentajaRepository ventajaRepository;

    public GetNpcDetailUseCase(NPCRepository npcRepository, VentajaRepository ventajaRepository) {
        this.npcRepository = npcRepository;
        this.ventajaRepository = ventajaRepository;
    }

    public NpcDetailDTO getNpcDetail(String npcId) {
        NPC npc = npcRepository.findById(npcId)
                .orElseThrow(() -> new RuntimeException("NPC no encontrado: " + npcId));

        List<Ventaja> ventajas = ventajaRepository.findByNpcNpcId(npcId);

        return new NpcDetailDTO(npc, ventajas);
    }
}
