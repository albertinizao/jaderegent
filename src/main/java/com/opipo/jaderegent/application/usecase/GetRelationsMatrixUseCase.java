package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.MatrixCellDTO;
import com.opipo.jaderegent.infrastructure.web.dto.RelationsMatrixDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetRelationsMatrixUseCase {
    private final PJRepository pjRepository;
    private final NPCRepository npcRepository;
    private final RelacionRepository relacionRepository;

    public GetRelationsMatrixUseCase(PJRepository pjRepository, NPCRepository npcRepository,
            RelacionRepository relacionRepository) {
        this.pjRepository = pjRepository;
        this.npcRepository = npcRepository;
        this.relacionRepository = relacionRepository;
    }

    public RelationsMatrixDTO getMatrix() {
        // Get all PJs
        List<PJ> pjs = pjRepository.findAll();
        List<RelationsMatrixDTO.PjSummaryDTO> pjSummaries = pjs.stream()
                .map(pj -> new RelationsMatrixDTO.PjSummaryDTO(
                        pj.getPjId(),
                        pj.getNombreDisplay(),
                        pj.getImagenUrl()))
                .collect(Collectors.toList());

        // Get all NPCs
        List<NPC> npcs = npcRepository.findAll();
        List<RelationsMatrixDTO.NpcSummaryDTO> npcSummaries = npcs.stream()
                .map(npc -> new RelationsMatrixDTO.NpcSummaryDTO(
                        npc.getNpcId(),
                        npc.getNombre(),
                        npc.getImagenUrl(),
                        npc.getNivelMaximo()))
                .collect(Collectors.toList());

        // Get all relations and build matrix
        List<Relacion> relaciones = relacionRepository.findAll();
        Map<String, MatrixCellDTO> matrix = new HashMap<>();

        for (Relacion rel : relaciones) {
            String key = rel.getPj().getPjId() + ":" + rel.getNpc().getNpcId();
            MatrixCellDTO cell = MatrixCellDTO.fromRelacion(
                    rel.getRelacionId(),
                    rel.getNivelActual(),
                    rel.getNpc().getNivelMaximo(),
                    rel.getContadorInteracciones());
            matrix.put(key, cell);
        }

        return new RelationsMatrixDTO(pjSummaries, npcSummaries, matrix);
    }
}
