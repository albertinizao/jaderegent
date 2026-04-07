package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.repository.PJRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdatePjUseCaseImplTest {

    @Mock
    private PJRepository pjRepository;

    @InjectMocks
    private UpdatePjUseCaseImpl useCase;

    @Test
    void updatePj_shouldUpdateOnlyProvidedFields() {
        UUID pjId = UUID.randomUUID();
        PJ pj = PJ.builder()
                .pjId(pjId)
                .nombreDisplay("Antes")
                .notaOpcional("nota vieja")
                .imagenUrl("img vieja")
                .build();

        when(pjRepository.findById(pjId)).thenReturn(Optional.of(pj));
        when(pjRepository.save(pj)).thenReturn(pj);

        PJ updated = useCase.updatePj(pjId, "Después", null, "img nueva");

        assertEquals("Después", updated.getNombreDisplay());
        assertEquals("nota vieja", updated.getNotaOpcional());
        assertEquals("img nueva", updated.getImagenUrl());
    }

    @Test
    void updatePj_shouldAllowClearingWithEmptyString() {
        UUID pjId = UUID.randomUUID();
        PJ pj = PJ.builder().pjId(pjId).nombreDisplay("Nombre").notaOpcional("nota").build();

        when(pjRepository.findById(pjId)).thenReturn(Optional.of(pj));
        when(pjRepository.save(pj)).thenReturn(pj);

        PJ updated = useCase.updatePj(pjId, "", "", null);

        assertEquals("", updated.getNombreDisplay());
        assertEquals("", updated.getNotaOpcional());
    }

    @Test
    void updatePj_shouldFailWhenPjDoesNotExist() {
        UUID pjId = UUID.randomUUID();
        when(pjRepository.findById(pjId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.updatePj(pjId, "n", "d", "i"));

        assertEquals("PJ no encontrado: " + pjId, ex.getMessage());
    }
}
