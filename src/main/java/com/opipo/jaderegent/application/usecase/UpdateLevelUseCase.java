package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Relacion;
import java.util.UUID;

public interface UpdateLevelUseCase {
    Relacion updateLevel(UUID relacionId, boolean increment);
}
