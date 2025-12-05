package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePjUseCase {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreatePjUseCase.class);

    private final PJRepository pjRepository;
    private final NPCRepository npcRepository;
    private final RelacionRepository relacionRepository;

    public CreatePjUseCase(PJRepository pjRepository, NPCRepository npcRepository,
            RelacionRepository relacionRepository) {
        this.pjRepository = pjRepository;
        this.npcRepository = npcRepository;
        this.relacionRepository = relacionRepository;
    }

    @Transactional
    public PJ createPj(String nombreDisplay, String notaOpcional, String imagenUrl) {
        if (pjRepository.existsByNombreDisplay(nombreDisplay)) {
            throw new IllegalArgumentException("Ya existe un PJ con el nombre: " + nombreDisplay);
        }

        PJ newPj = PJ.builder()
                .nombreDisplay(nombreDisplay)
                .notaOpcional(notaOpcional)
                .imagenUrl(imagenUrl)
                .fechaCreacion(LocalDateTime.now())
                .build();

        PJ savedPj = pjRepository.save(newPj);
        log.info("PJ creado con ID: {}", savedPj.getPjId());

        // Inicializar relaciones con todos los NPCs existentes
        List<NPC> allNpcs = npcRepository.findAll();
        for (NPC npc : allNpcs) {
            Relacion relacion = Relacion.builder()
                    .pj(savedPj)
                    .npc(npc)
                    .nivelActual(0)
                    .pendienteEleccion(false)
                    .consistente(true)
                    .contadorInteracciones(0)
                    .ultimaActualizacionTs(LocalDateTime.now())
                    .build();
            relacionRepository.save(relacion);
        }
        log.info("Inicializadas {} relaciones para el PJ {}", allNpcs.size(), savedPj.getNombreDisplay());

        return savedPj;
    }
}
