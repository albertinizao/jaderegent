package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.CreateRelacionUseCase;
import com.opipo.jaderegent.application.usecase.RegisterInteraccionUseCase;
import com.opipo.jaderegent.domain.model.Interaccion;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.infrastructure.web.dto.CreateInteraccionRequest;
import com.opipo.jaderegent.infrastructure.web.dto.RelacionDTO;
import com.opipo.jaderegent.infrastructure.web.dto.SeleccionVentajaDTO;
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
    private final com.opipo.jaderegent.application.usecase.DeleteRelacionUseCase deleteRelacionUseCase;

    public RelacionController(RegisterInteraccionUseCase registerInteraccionUseCase,
            CreateRelacionUseCase createRelacionUseCase,
            com.opipo.jaderegent.application.usecase.SelectVentajaUseCase selectVentajaUseCase,
            com.opipo.jaderegent.application.usecase.UpdateLevelUseCase updateLevelUseCase,
            com.opipo.jaderegent.application.usecase.DeleteRelacionUseCase deleteRelacionUseCase) {
        this.registerInteraccionUseCase = registerInteraccionUseCase;
        this.createRelacionUseCase = createRelacionUseCase;
        this.selectVentajaUseCase = selectVentajaUseCase;
        this.updateLevelUseCase = updateLevelUseCase;
        this.deleteRelacionUseCase = deleteRelacionUseCase;
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

    @org.springframework.web.bind.annotation.DeleteMapping("/{relacionId}")
    public ResponseEntity<Void> deleteRelacion(@PathVariable UUID relacionId) {
        deleteRelacionUseCase.delete(relacionId);
        return ResponseEntity.noContent().build();
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
                .selecciones(relacion.getSelecciones().stream()
                        .map(s -> SeleccionVentajaDTO.builder()
                                .ventajaId(s.getVentaja().getVentajaId())
                                .nombre(s.getVentaja().getNombre())
                                .nivelAdquisicion(s.getNivelAdquisicion())
                                .descripcion(s.getVentaja().getDescripcionLarga())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
