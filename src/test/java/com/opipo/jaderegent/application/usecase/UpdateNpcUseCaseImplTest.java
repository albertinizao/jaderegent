package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateNpcUseCaseImplTest {

    @Mock
    private NPCRepository npcRepository;

    @InjectMocks
    private UpdateNpcUseCaseImpl useCase;

    @Test
    void updateNpc_shouldUpdateOnlyProvidedFields() {
        NPC npc = NPC.builder()
                .npcId("npc-1")
                .nombre("Antes")
                .descripcionLarga("desc vieja")
                .imagenUrl("img vieja")
                .build();

        when(npcRepository.findById("npc-1")).thenReturn(Optional.of(npc));
        when(npcRepository.save(npc)).thenReturn(npc);

        NPC updated = useCase.updateNpc("npc-1", "Después", null, "img nueva");

        assertEquals("Después", updated.getNombre());
        assertEquals("desc vieja", updated.getDescripcionLarga());
        assertEquals("img nueva", updated.getImagenUrl());
    }

    @Test
    void updateNpc_shouldIgnoreBlankName() {
        NPC npc = NPC.builder().npcId("npc-1").nombre("Nombre Original").build();

        when(npcRepository.findById("npc-1")).thenReturn(Optional.of(npc));
        when(npcRepository.save(npc)).thenReturn(npc);

        NPC updated = useCase.updateNpc("npc-1", "   ", "desc", null);

        assertEquals("Nombre Original", updated.getNombre());
        assertEquals("desc", updated.getDescripcionLarga());
    }

    @Test
    void updateNpc_shouldFailWhenNpcDoesNotExist() {
        when(npcRepository.findById("missing")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.updateNpc("missing", "n", "d", "i"));

        assertEquals("NPC no encontrado: missing", ex.getMessage());
    }
}
