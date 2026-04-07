package com.opipo.jaderegent.infrastructure.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.application.usecase.CreatePjUseCase;
import com.opipo.jaderegent.application.usecase.DeletePjUseCase;
import com.opipo.jaderegent.application.usecase.GetPjDetailUseCase;
import com.opipo.jaderegent.application.usecase.GetPjPrintableAdvantagesUseCase;
import com.opipo.jaderegent.application.usecase.GetPjsUseCase;
import com.opipo.jaderegent.application.usecase.UpdatePjUseCase;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.infrastructure.web.dto.PjDTO;
import com.opipo.jaderegent.infrastructure.web.dto.PjDetailDTO;
import com.opipo.jaderegent.infrastructure.web.dto.PjPrintableAdvantagesDTO;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PjControllerTest {

    @Mock private CreatePjUseCase createPjUseCase;
    @Mock private GetPjsUseCase getPjsUseCase;
    @Mock private DeletePjUseCase deletePjUseCase;
    @Mock private GetPjDetailUseCase getPjDetailUseCase;
    @Mock private UpdatePjUseCase updatePjUseCase;
    @Mock private GetPjPrintableAdvantagesUseCase getPjPrintableAdvantagesUseCase;

    private PjController controller;

    @BeforeEach
    void setUp() {
        controller = new PjController(createPjUseCase, getPjsUseCase, deletePjUseCase, getPjDetailUseCase,
                updatePjUseCase, getPjPrintableAdvantagesUseCase);
    }

    @Test
    void getAllPjs_shouldMapDomainToDto() {
        UUID id = UUID.randomUUID();
        when(getPjsUseCase.getAllPjs()).thenReturn(List.of(PJ.builder()
                .pjId(id)
                .nombreDisplay("PJ")
                .notaOpcional("nota")
                .imagenUrl("img")
                .build()));

        ResponseEntity<List<PjDTO>> response = controller.getAllPjs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(id, response.getBody().getFirst().getPjId());
        assertEquals("PJ", response.getBody().getFirst().getNombreDisplay());
    }

    @Test
    void createPj_shouldReturnCreatedResponse() {
        UUID id = UUID.randomUUID();
        PjDTO request = PjDTO.builder().nombreDisplay("PJ").notaOpcional("n").imagenUrl("img").build();

        when(createPjUseCase.createPj("PJ", "n", "img"))
                .thenReturn(PJ.builder().pjId(id).nombreDisplay("PJ").notaOpcional("n").imagenUrl("img").build());

        ResponseEntity<PjDTO> response = controller.createPj(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/pj/" + id, response.getHeaders().getLocation().toString());
        assertEquals(id, response.getBody().getPjId());
    }

    @Test
    void getUpdateDeleteAndPrintable_shouldDelegateToUseCases() {
        UUID id = UUID.randomUUID();
        PjDetailDTO detail = PjDetailDTO.builder().pj(PjDTO.builder().pjId(id).nombreDisplay("PJ").build()).build();
        PjPrintableAdvantagesDTO printable = new PjPrintableAdvantagesDTO();

        when(getPjDetailUseCase.getPjDetail(id)).thenReturn(detail);
        when(updatePjUseCase.updatePj(id, "nuevo", "nota", "img"))
                .thenReturn(PJ.builder().pjId(id).nombreDisplay("nuevo").notaOpcional("nota").imagenUrl("img").build());
        when(getPjPrintableAdvantagesUseCase.execute(id)).thenReturn(printable);

        ResponseEntity<PjDetailDTO> detailResponse = controller.getPjDetail(id);
        ResponseEntity<PjDTO> updateResponse = controller.updatePj(id,
                PjDTO.builder().nombreDisplay("nuevo").notaOpcional("nota").imagenUrl("img").build());
        ResponseEntity<PjPrintableAdvantagesDTO> printableResponse = controller.getPjPrintableAdvantages(id);
        ResponseEntity<Void> deleteResponse = controller.deletePj(id);

        assertEquals(detail, detailResponse.getBody());
        assertEquals("nuevo", updateResponse.getBody().getNombreDisplay());
        assertEquals(printable, printableResponse.getBody());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        verify(deletePjUseCase).deletePj(id);
    }
}
