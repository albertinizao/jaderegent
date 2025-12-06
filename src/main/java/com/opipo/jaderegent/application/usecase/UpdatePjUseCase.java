package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.PJ;
import java.util.UUID;

public interface UpdatePjUseCase {
    PJ updatePj(UUID pjId, String nombreDisplay, String notaOpcional, String imagenUrl);
}
