package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.repository.PJRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPjsUseCase {

    private final PJRepository pjRepository;

    public GetPjsUseCase(PJRepository pjRepository) {
        this.pjRepository = pjRepository;
    }

    @Transactional(readOnly = true)
    public List<PJ> getAllPjs() {
        return pjRepository.findAll();
    }
}
