package com.opipo.jaderegent.infrastructure.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.application.usecase.CreateRelacionUseCase;
import com.opipo.jaderegent.application.usecase.DeleteRelacionUseCase;
import com.opipo.jaderegent.application.usecase.GetRelationsMatrixUseCase;
import com.opipo.jaderegent.application.usecase.RegisterInteraccionUseCase;
import com.opipo.jaderegent.application.usecase.SelectVentajaUseCase;
import com.opipo.jaderegent.application.usecase.UpdateLevelUseCase;
import com.opipo.jaderegent.domain.model.Interaccion;
import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.infrastructure.web.dto.CreateInteraccionRequest;
import com.opipo.jaderegent.infrastructure.web.dto.CreateRelacionRequest;
import com.opipo.jaderegent.infrastructure.web.dto.RelacionDTO;
import com.opipo.jaderegent.infrastructure.web.dto.RelationsMatrixDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RelacionControllerTest {

    @Mock private RegisterInteraccionUseCase registerInteraccionUseCase;
    @Mock private CreateRelacionUseCase createRelacionUseCase;
    @Mock private SelectVentajaUseCase selectVentajaUseCase;
    @Mock private UpdateLevelUseCase updateLevelUseCase;
    @Mock private DeleteRelacionUseCase deleteRelacionUseCase;
    @Mock private GetRelationsMatrixUseCase getRelationsMatrixUseCase;

    private RelacionController controller;

    @BeforeEach
    void setUp() {
        controller = new RelacionController(registerInteraccionUseCase, createRelacionUseCase, selectVentajaUseCase,
                updateLevelUseCase, deleteRelacionUseCase, getRelationsMatrixUseCase);
    }

    @Test
    void matrixAndDelete_shouldDelegate() {
        RelationsMatrixDTO matrix = new RelationsMatrixDTO(List.of(), List.of(), Map.of());
        when(getRelationsMatrixUseCase.getMatrix()).thenReturn(matrix);

        ResponseEntity<RelationsMatrixDTO> matrixResponse = controller.getRelationsMatrix();
        ResponseEntity<Void> deleteResponse = controller.deleteRelacion(UUID.randomUUID());

        assertEquals(matrix, matrixResponse.getBody());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    void createSelectAndUpdate_shouldMapRelacionToDto() {
        UUID relacionId = UUID.randomUUID();
        UUID pjId = UUID.randomUUID();

        Ventaja ventaja = Ventaja.builder().ventajaId("v1").nombre("Ventaja").descripcionLarga("desc").build();
        NPC npc = NPC.builder().npcId("npc-1").nombre("Ameiko").imagenUrl("npc-img").nivelMaximo(4).build();
        npc.setVentajas(new ArrayList<>(List.of(ventaja)));
        Relacion relacion = Relacion.builder()
                .nivelActual(1)
                .pendienteEleccion(false)
                .consistente(true)
                .contadorInteracciones(2)
                .pj(PJ.builder().pjId(pjId).nombreDisplay("PJ").build())
                .npc(npc)
                .selecciones(new ArrayList<>(List.of(SeleccionVentaja.builder()
                        .ventaja(ventaja)
                        .nivelAdquisicion(1)
                        .build())))
                .build();
        relacion.setRelacionId(relacionId);

        when(createRelacionUseCase.create(any(CreateRelacionRequest.class))).thenReturn(relacion);
        when(selectVentajaUseCase.select(relacionId, "v1")).thenReturn(relacion);
        when(updateLevelUseCase.updateLevel(relacionId, true)).thenReturn(relacion);

        ResponseEntity<RelacionDTO> createResponse = controller.createRelacion(new CreateRelacionRequest(pjId, "npc-1"));
        ResponseEntity<RelacionDTO> selectResponse = controller.selectVentaja(relacionId, "v1");
        ResponseEntity<RelacionDTO> updateResponse = controller.updateLevel(relacionId, true);

        assertRelacionDto(createResponse.getBody(), relacionId);
        assertRelacionDto(selectResponse.getBody(), relacionId);
        assertRelacionDto(updateResponse.getBody(), relacionId);
    }

    @Test
    void registerInteraccion_shouldUseMasterUser() {
        UUID relacionId = UUID.randomUUID();
        CreateInteraccionRequest request = CreateInteraccionRequest.builder()
                .tipo(Interaccion.TipoInteraccion.POSITIVA)
                .nota("ok")
                .build();
        Interaccion interaccion = Interaccion.builder().tipo(Interaccion.TipoInteraccion.POSITIVA).valor(1).build();

        when(registerInteraccionUseCase.register(relacionId, request, "master")).thenReturn(interaccion);

        ResponseEntity<Interaccion> response = controller.registerInteraccion(relacionId, request);

        assertEquals(interaccion, response.getBody());
        verify(registerInteraccionUseCase).register(relacionId, request, "master");
    }

    private static void assertRelacionDto(RelacionDTO dto, UUID relacionId) {
        assertNotNull(dto);
        assertEquals(relacionId, dto.getRelacionId());
        assertEquals("npc-1", dto.getNpcId());
        assertEquals("Ameiko", dto.getNpcNombre());
        assertEquals(1, dto.getNivelActual());
        assertEquals(List.of("v1"), dto.getVentajasObtenidasIds());
        assertEquals(1, dto.getSelecciones().size());
    }
}
