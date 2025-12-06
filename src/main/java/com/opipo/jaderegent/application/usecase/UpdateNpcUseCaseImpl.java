package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateNpcUseCaseImpl implements UpdateNpcUseCase {

    private final NPCRepository npcRepository;

    public UpdateNpcUseCaseImpl(NPCRepository npcRepository) {
        this.npcRepository = npcRepository;
    }

    @Override
    public NPC updateNpc(String npcId, String nombre, String descripcionLarga, String imagenUrl) {
        NPC npc = npcRepository.findById(npcId)
                .orElseThrow(() -> new IllegalArgumentException("NPC no encontrado: " + npcId));

        if (nombre != null && !nombre.trim().isEmpty()) {
            npc.setNombre(nombre);
        }
        if (descripcionLarga != null) {
            npc.setDescripcionLarga(descripcionLarga);
        }
        if (imagenUrl != null) {
            npc.setImagenUrl(imagenUrl);
        }

        return npcRepository.save(npc);
    }
}
