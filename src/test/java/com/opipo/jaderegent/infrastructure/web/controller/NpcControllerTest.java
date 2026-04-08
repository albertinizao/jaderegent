package com.opipo.jaderegent.infrastructure.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.application.usecase.DeleteNpcUseCase;
import com.opipo.jaderegent.application.usecase.GetNpcDetailUseCase;
import com.opipo.jaderegent.application.usecase.GetNpcsUseCase;
import com.opipo.jaderegent.application.usecase.ImportNpcUseCase;
import com.opipo.jaderegent.application.usecase.UpdateNpcUseCase;
import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.infrastructure.web.dto.NpcDetailDTO;
import com.opipo.jaderegent.infrastructure.web.dto.UpdateNpcRequest;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class NpcControllerTest {

    @Mock private ImportNpcUseCase importNpcUseCase;
    @Mock private GetNpcsUseCase getNpcsUseCase;
    @Mock private GetNpcDetailUseCase getNpcDetailUseCase;
    @Mock private DeleteNpcUseCase deleteNpcUseCase;
    @Mock private UpdateNpcUseCase updateNpcUseCase;

    private NpcController controller;

    @BeforeEach
    void setUp() {
        controller = new NpcController(importNpcUseCase, getNpcsUseCase, getNpcDetailUseCase, deleteNpcUseCase,
                updateNpcUseCase);
    }

    @Test
    void getAndUpdateAndDelete_shouldWork() {
        NPC npc = NPC.builder().npcId("npc-1").nombre("Ameiko").build();
        NpcDetailDTO detailDTO = new NpcDetailDTO();

        when(getNpcsUseCase.getAllNpcs()).thenReturn(List.of(npc));
        when(getNpcDetailUseCase.getNpcDetail("npc-1")).thenReturn(detailDTO);
        when(updateNpcUseCase.updateNpc("npc-1", "nuevo", "desc", "img")).thenReturn(npc);

        ResponseEntity<List<NPC>> all = controller.getAllNpcs();
        ResponseEntity<NpcDetailDTO> detail = controller.getNpcDetail("npc-1");
        ResponseEntity<NPC> update = controller.updateNpc("npc-1", new UpdateNpcRequest("nuevo", "desc", "img"));
        ResponseEntity<Void> delete = controller.deleteNpc("npc-1");

        assertEquals(1, all.getBody().size());
        assertEquals(detailDTO, detail.getBody());
        assertEquals(npc, update.getBody());
        assertEquals(HttpStatus.NO_CONTENT, delete.getStatusCode());
        verify(deleteNpcUseCase).deleteNpc("npc-1");
    }

    @Test
    void importNpc_shouldHandleEmptyAndIOException() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("file", "npc.json", "application/json", new byte[0]);
        ResponseEntity<String> emptyResponse = controller.importNpc(empty);
        assertEquals(HttpStatus.BAD_REQUEST, emptyResponse.getStatusCode());
        assertEquals("Fichero vacío", emptyResponse.getBody());

        MockMultipartFile file = new MockMultipartFile("file", "npc.json", "application/json", "{}".getBytes());
        doThrow(new IOException("fallo")).when(importNpcUseCase).importNpc(any(), eq("npc.json"));

        ResponseEntity<String> errorResponse = controller.importNpc(file);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
        assertEquals("Error al procesar el fichero JSON", errorResponse.getBody());
    }

    @Test
    void importNpc_shouldReturnOkWhenImportSucceeds() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "npc.json", "application/json", "{}".getBytes());

        ResponseEntity<String> response = controller.importNpc(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("NPC importado correctamente: npc.json", response.getBody());
        verify(importNpcUseCase).importNpc(any(), eq("npc.json"));
    }
}
