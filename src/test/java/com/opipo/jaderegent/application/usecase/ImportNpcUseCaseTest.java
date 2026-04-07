package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.VentajaRepository;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportNpcUseCaseTest {

    @Mock private NPCRepository npcRepository;
    @Mock private VentajaRepository ventajaRepository;

    @InjectMocks
    private ImportNpcUseCase useCase;

    @Test
    void importNpc_shouldPersistNpcAndReplaceAdvantages() throws Exception {
        useCase = new ImportNpcUseCase(npcRepository, ventajaRepository, new ObjectMapper());

        String json = """
                {
                  \"nombre\": \"Ameiko\",
                  \"descripcion_larga\": \"descripcion\",
                  \"nivel_maximo\": 6,
                  \"imagen_url\": \"img.png\",
                  \"ventajas\": [
                    {
                      \"nombre\": \"Corazón de dragón\",
                      \"descripcion_larga\": \"detalle\",
                      \"min_nivel_relacion\": 2,
                      \"prerequisitos\": [\"v_prev\"]
                    }
                  ]
                }
                """;

        when(ventajaRepository.findByNpcNpcId("ameiko")).thenReturn(List.of());

        useCase.importNpc(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), "ameiko.json");

        ArgumentCaptor<NPC> npcCaptor = ArgumentCaptor.forClass(NPC.class);
        verify(npcRepository).save(npcCaptor.capture());
        assertEquals("ameiko", npcCaptor.getValue().getNpcId());
        assertEquals("Ameiko", npcCaptor.getValue().getNombre());
        assertEquals(6, npcCaptor.getValue().getNivelMaximo());

        ArgumentCaptor<List<Ventaja>> ventajasCaptor = ArgumentCaptor.forClass(List.class);
        verify(ventajaRepository).saveAll(ventajasCaptor.capture());
        assertEquals(1, ventajasCaptor.getValue().size());
        Ventaja ventaja = ventajasCaptor.getValue().getFirst();
        assertTrue(ventaja.getVentajaId().startsWith("ameiko_"));
        assertEquals(List.of("v_prev"), ventaja.getPrerequisitos());
    }
}
