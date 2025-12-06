package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Interaccion;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.InteraccionRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.CreateInteraccionRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterInteraccionUseCaseImpl implements RegisterInteraccionUseCase {
    private final RelacionRepository relacionRepository;
    private final InteraccionRepository interaccionRepository;

    @Override
    @Transactional
    public Interaccion register(UUID relacionId, CreateInteraccionRequest request, String usuario) {
        Relacion relacion = relacionRepository.findById(relacionId)
                .orElseThrow(() -> new IllegalArgumentException("Relacion not found: " + relacionId));

        int change = 0;
        if (request.getTipo() == Interaccion.TipoInteraccion.POSITIVA) {
            change = 1;
        } else if (request.getTipo() == Interaccion.TipoInteraccion.NEGATIVA) {
            change = -1;
        }

        Integer currentCount = relacion.getContadorInteracciones();
        if (currentCount == null) {
            currentCount = 0;
        }

        relacion.setContadorInteracciones(currentCount + change);
        relacion.setUltimaActualizacionTs(LocalDateTime.now());
        relacionRepository.save(relacion);

        Interaccion interaccion = Interaccion.builder()
                .relacion(relacion)
                .tipo(request.getTipo())
                .valor(change)
                .nota(request.getNota())
                .usuario(usuario)
                .ts(LocalDateTime.now())
                .build();

        return interaccionRepository.save(interaccion);
    }
}
