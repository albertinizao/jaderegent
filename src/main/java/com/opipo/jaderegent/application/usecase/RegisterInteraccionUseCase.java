package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Interaccion;
import com.opipo.jaderegent.infrastructure.web.dto.CreateInteraccionRequest;
import java.util.UUID;

public interface RegisterInteraccionUseCase {
    Interaccion register(UUID relacionId, CreateInteraccionRequest request, String usuario);
}
