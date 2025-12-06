package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Relacion;
import java.util.UUID;

public interface SelectVentajaUseCase {
    Relacion select(UUID relacionId, String ventajaId);
}
