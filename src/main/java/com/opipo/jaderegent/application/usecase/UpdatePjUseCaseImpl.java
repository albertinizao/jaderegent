package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.repository.PJRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UpdatePjUseCaseImpl implements UpdatePjUseCase {

    private final PJRepository pjRepository;

    public UpdatePjUseCaseImpl(PJRepository pjRepository) {
        this.pjRepository = pjRepository;
    }

    @Override
    public PJ updatePj(UUID pjId, String nombreDisplay, String notaOpcional, String imagenUrl) {
        PJ pj = pjRepository.findById(pjId)
                .orElseThrow(() -> new IllegalArgumentException("PJ no encontrado: " + pjId));

        if (nombreDisplay != null) {
            pj.setNombreDisplay(nombreDisplay);
        }
        if (notaOpcional != null) {
            pj.setNotaOpcional(notaOpcional);
        }
        if (imagenUrl != null) {
            pj.setImagenUrl(imagenUrl);
        }

        return pjRepository.save(pj);
    }
}
