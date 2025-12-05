package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.CreatePjUseCase;
import com.opipo.jaderegent.application.usecase.GetPjsUseCase;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.infrastructure.web.dto.PjDTO;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pj")

public class PjController {

        private final CreatePjUseCase createPjUseCase;
        private final GetPjsUseCase getPjsUseCase;

        public PjController(CreatePjUseCase createPjUseCase, GetPjsUseCase getPjsUseCase) {
                this.createPjUseCase = createPjUseCase;
                this.getPjsUseCase = getPjsUseCase;
        }

        @GetMapping
        public ResponseEntity<List<PjDTO>> getAllPjs() {
                List<PJ> pjs = getPjsUseCase.getAllPjs();
                List<PjDTO> dtos = pjs.stream()
                                .map(pj -> PjDTO.builder()
                                                .nombreDisplay(pj.getNombreDisplay())
                                                .notaOpcional(pj.getNotaOpcional())
                                                .imagenUrl(pj.getImagenUrl())
                                                .build())
                                .toList();
                return ResponseEntity.ok(dtos);
        }

        @PostMapping
        public ResponseEntity<PjDTO> createPj(@RequestBody PjDTO pjDTO) {
                PJ created = createPjUseCase.createPj(
                                pjDTO.getNombreDisplay(),
                                pjDTO.getNotaOpcional(),
                                pjDTO.getImagenUrl());

                return ResponseEntity.created(URI.create("/api/pj/" + created.getPjId()))
                                .body(PjDTO.builder()
                                                .nombreDisplay(created.getNombreDisplay())
                                                .notaOpcional(created.getNotaOpcional())
                                                .imagenUrl(created.getImagenUrl())
                                                .build());
        }
}
