package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.CreateRelacionUseCase;
import com.opipo.jaderegent.application.usecase.RegisterInteraccionUseCase;
import com.opipo.jaderegent.domain.model.Interaccion;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.infrastructure.web.dto.CreateInteraccionRequest;
import com.opipo.jaderegent.infrastructure.web.dto.RelacionDTO;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relaciones")
public class RelacionController {
    private final RegisterInteraccionUseCase registerInteraccionUseCase;
    private final CreateRelacionUseCase createRelacionUseCase;
    private final com.opipo.jaderegent.application.usecase.SelectVentajaUseCase selectVentajaUseCase;
    private final com.opipo.jaderegent.application.usecase.UpdateLevelUseCase updateLevelUseCase;

    public RelacionController(RegisterInteraccionUseCase registerInteraccionUseCase,
            CreateRelacionUseCase createRelacionUseCase,
            com.opipo.jaderegent.application.usecase.SelectVentajaUseCase selectVentajaUseCase,
            com.opipo.jaderegent.application.usecase.UpdateLevelUseCase updateLevelUseCase) {
        this.registerInteraccionUseCase = registerInteraccionUseCase;
        this.createRelacionUseCase = createRelacionUseCase;
        this.selectVentajaUseCase = selectVentajaUseCase;
        this.updateLevelUseCase = updateLevelUseCase;
    }

    @PostMapping
    public ResponseEntity<RelacionDTO> createRelacion(
            @RequestBody com.opipo.jaderegent.infrastructure.web.dto.CreateRelacionRequest request) {
        Relacion relacion = createRelacionUseCase.create(request);
        return ResponseEntity.ok(toDTO(relacion));
    }

    @PostMapping("/{relacionId}/ventajas/{ventajaId}")
    public ResponseEntity<RelacionDTO> selectVentaja(
            @PathVariable UUID relacionId,
            @PathVariable String ventajaId) {
        Relacion relacion = selectVentajaUseCase.select(relacionId, ventajaId);
        return ResponseEntity.ok(toDTO(relacion));
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

    @org.springframework.web.bind.annotation.PatchMapping("/{relacionId}/nivel")
    public ResponseEntity<RelacionDTO> updateLevel(
            @PathVariable UUID relacionId,
            @org.springframework.web.bind.annotation.RequestParam boolean increment) {
        Relacion relacion = updateLevelUseCase.updateLevel(relacionId, increment);
        return ResponseEntity.ok(toDTO(relacion));
    }

    private RelacionDTO toDTO(Relacion relacion) {
        return RelacionDTO.builder()
                .relacionId(relacion.getRelacionId())
                .npcId(relacion.getNpc().getNpcId())
                .npcNombre(relacion.getNpc().getNombre())
                .npcImagenUrl(relacion.getNpc().getImagenUrl())
                .nivelActual(relacion.getNivelActual())
                .nivelMaximo(relacion.getNpc().getNivelMaximo())
                .pendienteEleccion(relacion.getPendienteEleccion())
                .consistente(relacion.getConsistente())
                .contadorInteracciones(relacion.getContadorInteracciones())
                .ventajasObtenidasIds(relacion.getVentajasObtenidas().stream()
                        .map(com.opipo.jaderegent.domain.model.Ventaja::getVentajaId)
                        .collect(Collectors.toList()))
                .build();
    }
}
