package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.PjDetailDTO;
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
class GetPjDetailUseCaseTest {

    @Mock private PJRepository pjRepository;
    @Mock private RelacionRepository relacionRepository;

    @InjectMocks
    private GetPjDetailUseCase useCase;

    @Test
    void getPjDetail_shouldMapAndSortRelationsByNpcName() {
        UUID pjId = UUID.randomUUID();
        PJ pj = PJ.builder().pjId(pjId).nombreDisplay("Renji").notaOpcional("n").imagenUrl("img").build();

        Ventaja v = Ventaja.builder().ventajaId("v1").nombre("Ventaja").minNivelRelacion(1).descripcionLarga("d").build();
        Relacion relB = Relacion.builder()
                .pj(pj)
                .npc(NPC.builder().npcId("b").nombre("Koya").imagenUrl("k").nivelMaximo(4).build())
                .nivelActual(1)
                .pendienteEleccion(false)
                .consistente(true)
                .contadorInteracciones(0)
                .selecciones(new ArrayList<>(List.of(SeleccionVentaja.builder().ventaja(v).nivelAdquisicion(1).build())))
                .build();
        Relacion relA = Relacion.builder()
                .pj(pj)
                .npc(NPC.builder().npcId("a").nombre("Ameiko").imagenUrl("a").nivelMaximo(5).build())
                .nivelActual(2)
                .pendienteEleccion(true)
                .consistente(true)
                .contadorInteracciones(1)
                .selecciones(new ArrayList<>())
                .build();

        when(pjRepository.findById(pjId)).thenReturn(Optional.of(pj));
        when(relacionRepository.findByPjPjId(pjId)).thenReturn(List.of(relB, relA));

        PjDetailDTO dto = useCase.getPjDetail(pjId);

        assertEquals("Renji", dto.getPj().getNombreDisplay());
        assertEquals(2, dto.getRelaciones().size());
        assertEquals("Ameiko", dto.getRelaciones().getFirst().getNpcNombre());
        assertEquals("Koya", dto.getRelaciones().get(1).getNpcNombre());
        assertEquals(List.of("v1"), dto.getRelaciones().get(1).getVentajasObtenidasIds());
    }

    @Test
    void getPjDetail_shouldFailWhenPjNotFound() {
        UUID pjId = UUID.randomUUID();
        when(pjRepository.findById(pjId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.getPjDetail(pjId));

        assertEquals("PJ no encontrado: " + pjId, ex.getMessage());
    }
}
