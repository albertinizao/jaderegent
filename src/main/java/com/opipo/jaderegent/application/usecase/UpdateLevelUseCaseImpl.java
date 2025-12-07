package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateLevelUseCaseImpl implements UpdateLevelUseCase {
    private final RelacionRepository relacionRepository;

    public UpdateLevelUseCaseImpl(RelacionRepository relacionRepository) {
        this.relacionRepository = relacionRepository;
    }

    @Override
    @Transactional
    public Relacion updateLevel(UUID relacionId, boolean increment) {
        Relacion relacion = relacionRepository.findById(relacionId)
                .orElseThrow(() -> new IllegalArgumentException("Relacion not found: " + relacionId));

        int currentLevel = relacion.getNivelActual() != null ? relacion.getNivelActual() : 0;
        int maxLevel = relacion.getNpc().getNivelMaximo();
        int newLevel = currentLevel;

        if (increment) {
            if (currentLevel < maxLevel) {
                newLevel++;
                // Trigger advantage selection only if leveling up
                relacion.setPendienteEleccion(true);
            }
        } else {
            if (currentLevel > 0) {
                newLevel--;
                // Decrementing does not trigger advantage selection,
                // but we might want to consider what happens if they had one pending.
                // For now, let's leave pending status as is, or maybe clear it?
                // SRS says "Subir nivel marca la relación como pendiente_eleccion=true".
                // It doesn't explicitly say to clear it on decrement, but likely we shouldn't
                // force a selection if we dropped a level.
                // However, to be safe and simple: only SET true on increment.
            }
        }

        relacion.setNivelActual(newLevel);
        relacion.setUltimaActualizacionTs(LocalDateTime.now());

        return relacionRepository.save(relacion);
    }
}
