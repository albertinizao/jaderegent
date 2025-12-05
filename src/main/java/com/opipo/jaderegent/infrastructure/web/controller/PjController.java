package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.CreatePjUseCase;
import com.opipo.jaderegent.application.usecase.GetPjsUseCase;
import com.opipo.jaderegent.application.usecase.DeletePjUseCase;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.infrastructure.web.dto.PjDTO;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pj")

public class PjController {

        private final CreatePjUseCase createPjUseCase;
        private final GetPjsUseCase getPjsUseCase;
        private final DeletePjUseCase deletePjUseCase;

        public PjController(CreatePjUseCase createPjUseCase, GetPjsUseCase getPjsUseCase,
                        DeletePjUseCase deletePjUseCase) {
                this.createPjUseCase = createPjUseCase;
                this.getPjsUseCase = getPjsUseCase;
                this.deletePjUseCase = deletePjUseCase;
        }

        @GetMapping
        public ResponseEntity<List<PjDTO>> getAllPjs() {
                List<PJ> pjs = getPjsUseCase.getAllPjs();
                List<PjDTO> dtos = pjs.stream()
                                .map(pj -> PjDTO.builder()
                                                .pjId(pj.getPjId())
                                                .nombreDisplay(pj.getNombreDisplay())
                                                .notaOpcional(pj.getNotaOpcional())
                                                .imagenUrl(pj.getImagenUrl())
                                                .build())
                                .toList();
                return ResponseEntity.ok(dtos);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletePj(@PathVariable UUID id) {
                deletePjUseCase.deletePj(id);
                return ResponseEntity.noContent().build();
        }

        @PostMapping
        public ResponseEntity<PjDTO> createPj(@RequestBody PjDTO pjDTO) {
                PJ created = createPjUseCase.createPj(
                                pjDTO.getNombreDisplay(),
                                pjDTO.getNotaOpcional(),
                                pjDTO.getImagenUrl());

                return ResponseEntity.created(URI.create("/api/pj/" + created.getPjId()))
                                .body(PjDTO.builder()
                                                .pjId(created.getPjId())
                                                .nombreDisplay(created.getNombreDisplay())
                                                .notaOpcional(created.getNotaOpcional())
                                                .imagenUrl(created.getImagenUrl())
                                                .build());
        }
}
