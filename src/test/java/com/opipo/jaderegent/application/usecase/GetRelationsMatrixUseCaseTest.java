package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.MatrixCellDTO;
import com.opipo.jaderegent.infrastructure.web.dto.RelationsMatrixDTO;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetRelationsMatrixUseCaseTest {

    @Mock private PJRepository pjRepository;
    @Mock private NPCRepository npcRepository;
    @Mock private RelacionRepository relacionRepository;

    @InjectMocks
    private GetRelationsMatrixUseCase useCase;

    @Test
    void getMatrix_shouldBuildSummariesAndMatrixCells() {
        UUID pjId = UUID.randomUUID();
        UUID relId = UUID.randomUUID();

        PJ pj = PJ.builder().pjId(pjId).nombreDisplay("Renji").imagenUrl("pj-img").build();
        NPC npc = NPC.builder().npcId("npc-1").nombre("Ameiko").imagenUrl("npc-img").nivelMaximo(5).build();
        Relacion relacion = Relacion.builder()
                .pj(pj)
                .npc(npc)
                .nivelActual(2)
                .contadorInteracciones(1)
                .build();
        relacion.setRelacionId(relId);

        when(pjRepository.findAll()).thenReturn(List.of(pj));
        when(npcRepository.findAll()).thenReturn(List.of(npc));
        when(relacionRepository.findAll()).thenReturn(List.of(relacion));

        RelationsMatrixDTO result = useCase.getMatrix();

        assertEquals(1, result.getPjs().size());
        assertEquals(1, result.getNpcs().size());
        MatrixCellDTO cell = result.getMatrix().get(pjId + ":npc-1");
        assertNotNull(cell);
        assertEquals(relId, cell.getRelacionId());
        assertEquals(2, cell.getNivelActual());
        assertEquals(5, cell.getNivelMaximo());
    }
}
