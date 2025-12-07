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
                // Reset interaction counter when leveling up
                relacion.setContadorInteracciones(0);
            }
        } else {
            if (currentLevel > 0) {
                newLevel--;
                // Logic to remove advantages that require a higher level than the new level
                int finalNewLevel = newLevel;
                relacion.getSelecciones().removeIf(s -> s.getNivelAdquisicion() > finalNewLevel);
            }
        }

        relacion.setNivelActual(newLevel);
        relacion.setUltimaActualizacionTs(LocalDateTime.now());

        return relacionRepository.save(relacion);
    }
}




