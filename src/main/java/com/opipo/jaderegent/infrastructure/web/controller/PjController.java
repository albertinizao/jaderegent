package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.CreatePjUseCase;
import com.opipo.jaderegent.application.usecase.GetPjsUseCase;
import com.opipo.jaderegent.application.usecase.GetPjDetailUseCase;
import com.opipo.jaderegent.application.usecase.DeletePjUseCase;
import com.opipo.jaderegent.application.usecase.UpdatePjUseCase;
import com.opipo.jaderegent.application.usecase.GetPjPrintableAdvantagesUseCase;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.infrastructure.web.dto.PjDTO;
import com.opipo.jaderegent.infrastructure.web.dto.PjDetailDTO;
import com.opipo.jaderegent.infrastructure.web.dto.PjPrintableAdvantagesDTO;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        private final GetPjDetailUseCase getPjDetailUseCase;
        private final UpdatePjUseCase updatePjUseCase;
        private final GetPjPrintableAdvantagesUseCase getPjPrintableAdvantagesUseCase;

        public PjController(CreatePjUseCase createPjUseCase, GetPjsUseCase getPjsUseCase,
                        DeletePjUseCase deletePjUseCase, GetPjDetailUseCase getPjDetailUseCase,
                        UpdatePjUseCase updatePjUseCase,
                        GetPjPrintableAdvantagesUseCase getPjPrintableAdvantagesUseCase) {
                this.createPjUseCase = createPjUseCase;
                this.getPjsUseCase = getPjsUseCase;
                this.deletePjUseCase = deletePjUseCase;
                this.getPjDetailUseCase = getPjDetailUseCase;
                this.updatePjUseCase = updatePjUseCase;
                this.getPjPrintableAdvantagesUseCase = getPjPrintableAdvantagesUseCase;
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

        @GetMapping("/{id}")
        public ResponseEntity<PjDetailDTO> getPjDetail(@PathVariable UUID id) {
                return ResponseEntity.ok(getPjDetailUseCase.getPjDetail(id));
        }

        @PutMapping("/{id}")
        public ResponseEntity<PjDTO> updatePj(@PathVariable UUID id, @RequestBody PjDTO pjDTO) {
                PJ updated = updatePjUseCase.updatePj(
                                id,
                                pjDTO.getNombreDisplay(),
                                pjDTO.getNotaOpcional(),
                                pjDTO.getImagenUrl());

                return ResponseEntity.ok(PjDTO.builder()
                                .pjId(updated.getPjId())
                                .nombreDisplay(updated.getNombreDisplay())
                                .notaOpcional(updated.getNotaOpcional())
                                .imagenUrl(updated.getImagenUrl())
                                .build());
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

        @GetMapping("/{id}/printable-advantages")
        public ResponseEntity<PjPrintableAdvantagesDTO> getPjPrintableAdvantages(@PathVariable UUID id) {
                return ResponseEntity.ok(getPjPrintableAdvantagesUseCase.execute(id));
        }
}
