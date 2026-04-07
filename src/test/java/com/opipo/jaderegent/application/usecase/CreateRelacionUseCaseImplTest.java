package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.CreateRelacionRequest;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRelacionUseCaseImplTest {

    @Mock
    private PJRepository pjRepository;
    @Mock
    private NPCRepository npcRepository;
    @Mock
    private RelacionRepository relacionRepository;

    @InjectMocks
    private CreateRelacionUseCaseImpl useCase;

    @Test
    void create_shouldPersistNewRelationWhenDataIsValid() {
        UUID pjId = UUID.randomUUID();
        String npcId = "npc-1";
        CreateRelacionRequest request = new CreateRelacionRequest(pjId, npcId);

        PJ pj = PJ.builder().pjId(pjId).nombreDisplay("Jade").build();
        NPC npc = NPC.builder().npcId(npcId).nombre("Ameiko").build();

        when(pjRepository.findById(pjId)).thenReturn(Optional.of(pj));
        when(npcRepository.findById(npcId)).thenReturn(Optional.of(npc));
        when(relacionRepository.findByPjPjIdAndNpcNpcId(pjId, npcId)).thenReturn(Optional.empty());
        when(relacionRepository.save(any(Relacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Relacion saved = useCase.create(request);

        assertEquals(pj, saved.getPj());
        assertEquals(npc, saved.getNpc());
        assertEquals(0, saved.getNivelActual());
        assertFalse(saved.getPendienteEleccion());
        assertFalse(saved.getContadorInteracciones() != 0);
        assertNotNull(saved.getUltimaActualizacionTs());
        verify(relacionRepository).save(any(Relacion.class));
    }

    @Test
    void create_shouldFailWhenPjDoesNotExist() {
        UUID pjId = UUID.randomUUID();
        when(pjRepository.findById(pjId)).thenReturn(Optional.empty());

        CreateRelacionRequest request = new CreateRelacionRequest(pjId, "npc-1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.create(request));

        assertEquals("PJ no encontrado: " + pjId, ex.getMessage());
    }

    @Test
    void create_shouldFailWhenNpcDoesNotExist() {
        UUID pjId = UUID.randomUUID();
        String npcId = "npc-404";
        when(pjRepository.findById(pjId)).thenReturn(Optional.of(PJ.builder().pjId(pjId).nombreDisplay("X").build()));
        when(npcRepository.findById(npcId)).thenReturn(Optional.empty());

        CreateRelacionRequest request = new CreateRelacionRequest(pjId, npcId);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.create(request));

        assertEquals("NPC no encontrado: " + npcId, ex.getMessage());
    }

    @Test
    void create_shouldFailWhenRelationAlreadyExists() {
        UUID pjId = UUID.randomUUID();
        String npcId = "npc-1";

        when(pjRepository.findById(pjId)).thenReturn(Optional.of(PJ.builder().pjId(pjId).nombreDisplay("X").build()));
        when(npcRepository.findById(npcId)).thenReturn(Optional.of(NPC.builder().npcId(npcId).nombre("Y").build()));
        when(relacionRepository.findByPjPjIdAndNpcNpcId(pjId, npcId)).thenReturn(Optional.of(Relacion.builder().build()));

        CreateRelacionRequest request = new CreateRelacionRequest(pjId, npcId);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.create(request));

        assertEquals("La relación ya existe", ex.getMessage());
    }
}
