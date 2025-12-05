package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.GetNpcsUseCase;
import com.opipo.jaderegent.application.usecase.GetNpcDetailUseCase;
import com.opipo.jaderegent.application.usecase.ImportNpcUseCase;
import com.opipo.jaderegent.application.usecase.DeleteNpcUseCase;
import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.infrastructure.web.dto.NpcDetailDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/npc")
public class NpcController {

    private final ImportNpcUseCase importNpcUseCase;
    private final GetNpcsUseCase getNpcsUseCase;
    private final GetNpcDetailUseCase getNpcDetailUseCase;
    private final DeleteNpcUseCase deleteNpcUseCase;

    public NpcController(ImportNpcUseCase importNpcUseCase, GetNpcsUseCase getNpcsUseCase,
            GetNpcDetailUseCase getNpcDetailUseCase, DeleteNpcUseCase deleteNpcUseCase) {
        this.importNpcUseCase = importNpcUseCase;
        this.getNpcsUseCase = getNpcsUseCase;
        this.getNpcDetailUseCase = getNpcDetailUseCase;
        this.deleteNpcUseCase = deleteNpcUseCase;
    }

    @GetMapping
    public ResponseEntity<List<NPC>> getAllNpcs() {
        return ResponseEntity.ok(getNpcsUseCase.getAllNpcs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NpcDetailDTO> getNpcDetail(@PathVariable String id) {
        return ResponseEntity.ok(getNpcDetailUseCase.getNpcDetail(id));
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
            return ResponseEntity.internalServerError().body("Error al procesar el fichero JSON: " + e.getMessage());
        }
    }
}
