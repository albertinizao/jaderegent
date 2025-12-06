package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.infrastructure.web.dto.CreateRelacionRequest;

public interface CreateRelacionUseCase {
    Relacion create(CreateRelacionRequest request);
}
