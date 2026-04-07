package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.domain.repository.VentajaRepository;
import com.opipo.jaderegent.infrastructure.web.dto.NpcDetailDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetNpcDetailUseCaseTest {

    @Mock private NPCRepository npcRepository;
    @Mock private VentajaRepository ventajaRepository;
    @Mock private RelacionRepository relacionRepository;

    @InjectMocks
    private GetNpcDetailUseCase useCase;

    @Test
    void getNpcDetail_shouldMapVentajasAndRelationsIncludingPjsConVentaja() {
        String npcId = "npc-1";
        UUID pjId = UUID.randomUUID();

        NPC npc = NPC.builder().npcId(npcId).nombre("Ameiko").descripcionLarga("desc").imagenUrl("img").nivelMaximo(6).build();
        Ventaja ventaja = Ventaja.builder().ventajaId("v1").nombre("Aliada").descripcionLarga("desc v").minNivelRelacion(1)
                .prerequisitos(List.of()).prerequisitosOperator("AND").build();

        Relacion relacion = Relacion.builder()
                .pj(PJ.builder().pjId(pjId).nombreDisplay("Renji").imagenUrl("pj-img").build())
                .npc(npc)
                .nivelActual(2)
                .pendienteEleccion(false)
                .contadorInteracciones(1)
                .selecciones(new ArrayList<>(List.of(SeleccionVentaja.builder().ventaja(ventaja).nivelAdquisicion(1).build())))
                .build();

        when(npcRepository.findById(npcId)).thenReturn(Optional.of(npc));
        when(ventajaRepository.findByNpcNpcId(npcId)).thenReturn(List.of(ventaja));
        when(relacionRepository.findByNpcNpcId(npcId)).thenReturn(List.of(relacion));

        NpcDetailDTO dto = useCase.getNpcDetail(npcId);

        assertEquals("Ameiko", dto.getNpc().getNombre());
        assertEquals(1, dto.getVentajas().size());
        assertEquals(List.of("Renji"), dto.getVentajas().getFirst().getPjsConVentaja());
        assertEquals(1, dto.getRelaciones().size());
        assertEquals("Renji", dto.getRelaciones().getFirst().getPjNombre());
    }

    @Test
    void getNpcDetail_shouldFailWhenNpcNotFound() {
        when(npcRepository.findById("missing")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.getNpcDetail("missing"));

        assertEquals("NPC no encontrado: missing", ex.getMessage());
    }
}
