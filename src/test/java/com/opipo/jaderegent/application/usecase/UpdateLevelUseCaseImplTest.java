package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class UpdateLevelUseCaseImplTest {

    @Mock
    private RelacionRepository relacionRepository;

    @InjectMocks
    private UpdateLevelUseCaseImpl useCase;

    @Test
    void updateLevel_shouldIncreaseLevelAndResetCounter() {
        UUID relacionId = UUID.randomUUID();
        NPC npc = NPC.builder().npcId("npc").nombre("NPC").nivelMaximo(5).build();
        Relacion relacion = Relacion.builder()
                .npc(npc)
                .nivelActual(1)
                .contadorInteracciones(3)
                .pendienteEleccion(false)
                .selecciones(new ArrayList<>())
                .build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));
        when(relacionRepository.save(relacion)).thenReturn(relacion);

        Relacion result = useCase.updateLevel(relacionId, true);

        assertEquals(2, result.getNivelActual());
        assertTrue(result.getPendienteEleccion());
        assertEquals(0, result.getContadorInteracciones());
    }

    @Test
    void updateLevel_shouldNotIncreaseAboveMaxLevel() {
        UUID relacionId = UUID.randomUUID();
        NPC npc = NPC.builder().npcId("npc").nombre("NPC").nivelMaximo(2).build();
        Relacion relacion = Relacion.builder().npc(npc).nivelActual(2).pendienteEleccion(false).build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));
        when(relacionRepository.save(relacion)).thenReturn(relacion);

        Relacion result = useCase.updateLevel(relacionId, true);

        assertEquals(2, result.getNivelActual());
    }

    @Test
    void updateLevel_shouldDecreaseAndDropSelectionsAboveNewLevel() {
        UUID relacionId = UUID.randomUUID();
        NPC npc = NPC.builder().npcId("npc").nombre("NPC").nivelMaximo(5).build();
        Ventaja v = Ventaja.builder().ventajaId("v").minNivelRelacion(1).build();
        List<SeleccionVentaja> selecciones = new ArrayList<>(List.of(
                SeleccionVentaja.builder().ventaja(v).nivelAdquisicion(1).build(),
                SeleccionVentaja.builder().ventaja(v).nivelAdquisicion(2).build(),
                SeleccionVentaja.builder().ventaja(v).nivelAdquisicion(3).build()));

        Relacion relacion = Relacion.builder().npc(npc).nivelActual(3).selecciones(selecciones).build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));
        when(relacionRepository.save(relacion)).thenReturn(relacion);

        Relacion result = useCase.updateLevel(relacionId, false);

        assertEquals(2, result.getNivelActual());
        assertEquals(2, result.getSelecciones().size());
    }

    @Test
    void updateLevel_shouldFailWhenRelationDoesNotExist() {
        UUID relacionId = UUID.randomUUID();
        when(relacionRepository.findById(relacionId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.updateLevel(relacionId, true));

        assertEquals("Relacion not found: " + relacionId, ex.getMessage());
    }
}
