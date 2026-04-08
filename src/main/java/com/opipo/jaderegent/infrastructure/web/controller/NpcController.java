package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.GetNpcsUseCase;
import com.opipo.jaderegent.application.usecase.GetNpcDetailUseCase;
import com.opipo.jaderegent.application.usecase.ImportNpcUseCase;
import com.opipo.jaderegent.application.usecase.DeleteNpcUseCase;
import com.opipo.jaderegent.application.usecase.UpdateNpcUseCase;
import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.infrastructure.web.dto.NpcDetailDTO;
import com.opipo.jaderegent.infrastructure.web.dto.UpdateNpcRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/npc")
public class NpcController {

    private static final Logger logger = LoggerFactory.getLogger(NpcController.class);

    private final ImportNpcUseCase importNpcUseCase;
    private final GetNpcsUseCase getNpcsUseCase;
    private final GetNpcDetailUseCase getNpcDetailUseCase;
    private final DeleteNpcUseCase deleteNpcUseCase;
    private final UpdateNpcUseCase updateNpcUseCase;

    public NpcController(ImportNpcUseCase importNpcUseCase, GetNpcsUseCase getNpcsUseCase,
            GetNpcDetailUseCase getNpcDetailUseCase, DeleteNpcUseCase deleteNpcUseCase,
            UpdateNpcUseCase updateNpcUseCase) {
        this.importNpcUseCase = importNpcUseCase;
        this.getNpcsUseCase = getNpcsUseCase;
        this.getNpcDetailUseCase = getNpcDetailUseCase;
        this.deleteNpcUseCase = deleteNpcUseCase;
        this.updateNpcUseCase = updateNpcUseCase;
    }

    @GetMapping
    public ResponseEntity<List<NPC>> getAllNpcs() {
        return ResponseEntity.ok(getNpcsUseCase.getAllNpcs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NpcDetailDTO> getNpcDetail(@PathVariable String id) {
        return ResponseEntity.ok(getNpcDetailUseCase.getNpcDetail(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NPC> updateNpc(@PathVariable String id, @RequestBody UpdateNpcRequest request) {
        NPC updatedNpc = updateNpcUseCase.updateNpc(
                id,
                request.getNombre(),
                request.getDescripcionLarga(),
                request.getImagenUrl());
        return ResponseEntity.ok(updatedNpc);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNpc(@PathVariable String id) {
        deleteNpcUseCase.deleteNpc(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<String> importNpc(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichero vacío");
        }
        try {
            importNpcUseCase.importNpc(file.getInputStream(), file.getOriginalFilename());
            return ResponseEntity.ok("NPC importado correctamente: " + file.getOriginalFilename());
        } catch (IOException e) {
            logger.error("Error al procesar el fichero JSON durante la importación de NPCs", e);
            return ResponseEntity.internalServerError().body("Error al procesar el fichero JSON: " + e.getMessage());
        }
    }
}
