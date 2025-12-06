package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.NPC;

public interface UpdateNpcUseCase {
    NPC updateNpc(String npcId, String nombre, String descripcionLarga, String imagenUrl);
}
