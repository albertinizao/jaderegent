package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.CreateRelacionUseCase;
import com.opipo.jaderegent.application.usecase.RegisterInteraccionUseCase;
import com.opipo.jaderegent.domain.model.Interaccion;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.infrastructure.web.dto.CreateInteraccionRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relaciones")
@RequiredArgsConstructor
public class RelacionController {
    private final RegisterInteraccionUseCase registerInteraccionUseCase;
    private final CreateRelacionUseCase createRelacionUseCase;

    @PostMapping
    public ResponseEntity<Relacion> createRelacion(
            @RequestBody com.opipo.jaderegent.infrastructure.web.dto.CreateRelacionRequest request) {
        Relacion relacion = createRelacionUseCase.create(request);
        return ResponseEntity.ok(relacion);
    }

    @PostMapping("/{relacionId}/interacciones")
    public ResponseEntity<Interaccion> registerInteraccion(
            @PathVariable UUID relacionId,
            @RequestBody CreateInteraccionRequest request) {

        // TODO: Get real user from security context
        String usuario = "master";

        Interaccion interaccion = registerInteraccionUseCase.register(relacionId, request, usuario);
        return ResponseEntity.ok(interaccion);
    }
}
