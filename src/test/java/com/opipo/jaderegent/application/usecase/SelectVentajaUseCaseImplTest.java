package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
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
class SelectVentajaUseCaseImplTest {

    @Mock
    private RelacionRepository relacionRepository;

    @InjectMocks
    private SelectVentajaUseCaseImpl useCase;

    @Test
    void select_shouldAddSelectionAndClearPendingWhenCaughtUp() {
        UUID relacionId = UUID.randomUUID();
        Ventaja ventaja = Ventaja.builder().ventajaId("v1").minNivelRelacion(1).build();
        NPC npc = NPC.builder().npcId("npc").nombre("NPC").build();
        npc.setVentajas(new ArrayList<>(List.of(ventaja)));

        Relacion relacion = Relacion.builder()
                .npc(npc)
                .nivelActual(1)
                .pendienteEleccion(true)
                .selecciones(new ArrayList<>())
                .build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));
        when(relacionRepository.save(relacion)).thenReturn(relacion);

        Relacion result = useCase.select(relacionId, "v1");

        assertEquals(1, result.getSelecciones().size());
        SeleccionVentaja seleccion = result.getSelecciones().getFirst();
        assertEquals(1, seleccion.getNivelAdquisicion());
        assertFalse(result.getPendienteEleccion());
    }

    @Test
    void select_shouldKeepPendingWhenStillMissingSelections() {
        UUID relacionId = UUID.randomUUID();
        Ventaja ventaja = Ventaja.builder().ventajaId("v2").minNivelRelacion(1).build();
        NPC npc = NPC.builder().npcId("npc").nombre("NPC").build();
        npc.setVentajas(new ArrayList<>(List.of(ventaja)));

        List<SeleccionVentaja> existing = new ArrayList<>();
        existing.add(SeleccionVentaja.builder().ventaja(ventaja).nivelAdquisicion(1).build());

        Relacion relacion = Relacion.builder()
                .npc(npc)
                .nivelActual(3)
                .pendienteEleccion(true)
                .selecciones(existing)
                .build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));
        when(relacionRepository.save(relacion)).thenReturn(relacion);

        Relacion result = useCase.select(relacionId, "v2");

        assertEquals(2, result.getSelecciones().size());
        assertEquals(true, result.getPendienteEleccion());
    }

    @Test
    void select_shouldFailWhenRequirementsAreNotMet() {
        UUID relacionId = UUID.randomUUID();
        Ventaja base = Ventaja.builder().ventajaId("base").minNivelRelacion(1).build();
        Ventaja locked = Ventaja.builder()
                .ventajaId("locked")
                .minNivelRelacion(1)
                .prerequisitos(List.of("missing"))
                .prerequisitosOperator("AND")
                .build();
        NPC npc = NPC.builder().npcId("npc").nombre("NPC").build();
        npc.setVentajas(new ArrayList<>(List.of(base, locked)));

        Relacion relacion = Relacion.builder()
                .npc(npc)
                .nivelActual(2)
                .pendienteEleccion(true)
                .selecciones(new ArrayList<>(List.of(SeleccionVentaja.builder().ventaja(base).nivelAdquisicion(1).build())))
                .build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> useCase.select(relacionId, "locked"));

        assertEquals("No se cumplen todos los prerrequisitos (AND).", ex.getMessage());
    }

    @Test
    void select_shouldFailWhenNoPendingSelection() {
        UUID relacionId = UUID.randomUUID();
        NPC npc = NPC.builder().npcId("npc").nombre("NPC").build();
        npc.setVentajas(new ArrayList<>());

        Relacion relacion = Relacion.builder()
                .npc(npc)
                .nivelActual(1)
                .pendienteEleccion(false)
                .selecciones(new ArrayList<>())
                .build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> useCase.select(relacionId, "v1"));

        assertEquals("Esta relación no tiene elecciones pendientes.", ex.getMessage());
    }
}
