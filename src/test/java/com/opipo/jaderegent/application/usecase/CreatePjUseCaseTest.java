package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatePjUseCaseTest {

    @Mock private PJRepository pjRepository;
    @Mock private NPCRepository npcRepository;
    @Mock private RelacionRepository relacionRepository;

    @InjectMocks
    private CreatePjUseCase useCase;

    @Test
    void createPj_shouldCreatePjAndInitializeRelations() {
        UUID pjId = UUID.randomUUID();
        when(pjRepository.existsByNombreDisplay("Nuevo PJ")).thenReturn(false);
        when(pjRepository.save(any(PJ.class))).thenAnswer(invocation -> {
            PJ pj = invocation.getArgument(0);
            pj.setPjId(pjId);
            return pj;
        });
        when(npcRepository.findAll()).thenReturn(List.of(
                NPC.builder().npcId("npc-1").nombre("Ameiko").nivelMaximo(3).build(),
                NPC.builder().npcId("npc-2").nombre("Koya").nivelMaximo(4).build()));

        PJ created = useCase.createPj("Nuevo PJ", "nota", "img");

        assertEquals(pjId, created.getPjId());
        assertEquals("Nuevo PJ", created.getNombreDisplay());
        verify(relacionRepository, times(2)).save(any());
    }

    @Test
    void createPj_shouldFailWhenNameAlreadyExists() {
        when(pjRepository.existsByNombreDisplay("Duplicado")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.createPj("Duplicado", "nota", "img"));

        assertEquals("Ya existe un PJ con el nombre: Duplicado", ex.getMessage());
    }
}
