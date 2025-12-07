package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import java.util.UUID;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SelectVentajaUseCaseImpl implements SelectVentajaUseCase {
    private final RelacionRepository relacionRepository;

    public SelectVentajaUseCaseImpl(RelacionRepository relacionRepository) {
        this.relacionRepository = relacionRepository;
    }

    @Override
    @Transactional
    public Relacion select(UUID relacionId, String ventajaId) {
        Relacion relacion = relacionRepository.findById(relacionId)
                .orElseThrow(() -> new IllegalArgumentException("Relacion no encontrada: " + relacionId));

        if (!relacion.getPendienteEleccion()) {
            throw new IllegalStateException("Esta relación no tiene elecciones pendientes.");
        }

        Ventaja ventaja = relacion.getNpc().getVentajas().stream()
                .filter(v -> v.getVentajaId().equals(ventajaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ventaja no encontrada en este NPC: " + ventajaId));

        validateRequirements(relacion, ventaja);

        com.opipo.jaderegent.domain.model.SeleccionVentaja seleccion = com.opipo.jaderegent.domain.model.SeleccionVentaja
                .builder()
                .relacion(relacion)
                .ventaja(ventaja)
                .nivelAdquisicion(relacion.getNivelActual())
                .build();

        relacion.getSelecciones().add(seleccion);
        relacion.setPendienteEleccion(false);

        return relacionRepository.save(relacion);
    }

    private void validateRequirements(Relacion relacion, Ventaja ventaja) {
        // 1. Check Level
        if (relacion.getNivelActual() < ventaja.getMinNivelRelacion()) {
            throw new IllegalStateException("Nivel de relación insuficiente. Requerido: " +
                    ventaja.getMinNivelRelacion() + ", Actual: " + relacion.getNivelActual());
        }

        // 2. Check Prerequisites
        List<String> prerequisites = ventaja.getPrerequisitos();
        if (prerequisites == null || prerequisites.isEmpty()) {
            return;
        }

        Set<Ventaja> obtained = relacion.getVentajasObtenidas();
        boolean isOrLogic = "OR".equalsIgnoreCase(ventaja.getPrerequisitosOperator());

        if (isOrLogic) {
            boolean anyMet = prerequisites.stream()
                    .anyMatch(reqId -> obtained.stream().anyMatch(v -> v.getVentajaId().equals(reqId)));
            if (!anyMet) {
                throw new IllegalStateException("No se cumple ninguno de los prerrequisitos (OR).");
            }
        } else {
            // AND logic (default)
            boolean allMet = prerequisites.stream()
                    .allMatch(reqId -> obtained.stream().anyMatch(v -> v.getVentajaId().equals(reqId)));
            if (!allMet) {
                throw new IllegalStateException("No se cumplen todos los prerrequisitos (AND).");
            }
        }
    }
}
