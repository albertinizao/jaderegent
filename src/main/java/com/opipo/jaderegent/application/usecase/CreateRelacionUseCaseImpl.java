package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.CreateRelacionRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateRelacionUseCaseImpl implements CreateRelacionUseCase {

    private final PJRepository pjRepository;
    private final NPCRepository npcRepository;
    private final RelacionRepository relacionRepository;

    public CreateRelacionUseCaseImpl(PJRepository pjRepository,
            NPCRepository npcRepository,
            RelacionRepository relacionRepository) {
        this.pjRepository = pjRepository;
        this.npcRepository = npcRepository;
        this.relacionRepository = relacionRepository;
    }

    @Override
    @Transactional
    public Relacion create(CreateRelacionRequest request) {
        PJ pj = pjRepository.findById(request.getPjId())
                .orElseThrow(() -> new IllegalArgumentException("PJ no encontrado: " + request.getPjId()));

        NPC npc = npcRepository.findById(request.getNpcId())
                .orElseThrow(() -> new IllegalArgumentException("NPC no encontrado: " + request.getNpcId()));

        Optional<Relacion> existing = relacionRepository.findByPjPjIdAndNpcNpcId(pj.getPjId(), npc.getNpcId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("La relación ya existe");
        }

        Relacion nuevaRelacion = Relacion.builder()
                .pj(pj)
                .npc(npc)
                .nivelActual(0)
                .pendienteEleccion(false)
                .consistente(true)
                .contadorInteracciones(0)
                .ultimaActualizacionTs(LocalDateTime.now())
                .build();

        return relacionRepository.save(nuevaRelacion);
    }
}
