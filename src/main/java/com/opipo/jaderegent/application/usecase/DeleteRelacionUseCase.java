package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteRelacionUseCase {
    private final RelacionRepository relacionRepository;

    public DeleteRelacionUseCase(RelacionRepository relacionRepository) {
        this.relacionRepository = relacionRepository;
    }

    @Transactional
    public void delete(UUID relacionId) {
        Relacion relacion = relacionRepository.findById(relacionId)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));

        // Validar que la relación esté en nivel 0 y sin interacciones
        if (relacion.getNivelActual() != 0) {
            throw new IllegalStateException("Solo se pueden eliminar relaciones en nivel 0");
        }

        if (relacion.getContadorInteracciones() != 0) {
            throw new IllegalStateException("No se pueden eliminar relaciones con interacciones registradas");
        }

        relacionRepository.deleteById(relacionId);
    }
}
