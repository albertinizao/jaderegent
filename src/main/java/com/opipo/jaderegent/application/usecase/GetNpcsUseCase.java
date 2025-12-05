package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetNpcsUseCase {

    private final NPCRepository npcRepository;

    public GetNpcsUseCase(NPCRepository npcRepository) {
        this.npcRepository = npcRepository;
    }

    public List<NPC> getAllNpcs() {
        return npcRepository.findAll();
    }
}
