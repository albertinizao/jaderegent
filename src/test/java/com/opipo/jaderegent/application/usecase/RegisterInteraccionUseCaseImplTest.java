package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.Interaccion;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.InteraccionRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.CreateInteraccionRequest;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterInteraccionUseCaseImplTest {

    @Mock
    private RelacionRepository relacionRepository;
    @Mock
    private InteraccionRepository interaccionRepository;

    @InjectMocks
    private RegisterInteraccionUseCaseImpl useCase;

    @Test
    void register_shouldIncrementCounterOnPositiveInteraction() {
        UUID relacionId = UUID.randomUUID();
        Relacion relacion = Relacion.builder().contadorInteracciones(2).build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));
        when(interaccionRepository.save(any(Interaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateInteraccionRequest request = CreateInteraccionRequest.builder()
                .tipo(Interaccion.TipoInteraccion.POSITIVA)
                .nota("Ayudó al NPC")
                .build();

        Interaccion interaccion = useCase.register(relacionId, request, "tester");

        assertEquals(3, relacion.getContadorInteracciones());
        assertEquals(1, interaccion.getValor());
        assertEquals("tester", interaccion.getUsuario());
    }

    @Test
    void register_shouldDecrementCounterOnNegativeInteractionAndHandleNullCounter() {
        UUID relacionId = UUID.randomUUID();
        Relacion relacion = Relacion.builder().contadorInteracciones(null).build();

        when(relacionRepository.findById(relacionId)).thenReturn(Optional.of(relacion));
        when(interaccionRepository.save(any(Interaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateInteraccionRequest request = CreateInteraccionRequest.builder()
                .tipo(Interaccion.TipoInteraccion.NEGATIVA)
                .nota("Mala decisión")
                .build();

        Interaccion interaccion = useCase.register(relacionId, request, "tester");

        assertEquals(-1, relacion.getContadorInteracciones());
        assertEquals(-1, interaccion.getValor());
    }

    @Test
    void register_shouldFailWhenRelationDoesNotExist() {
        UUID relacionId = UUID.randomUUID();
        when(relacionRepository.findById(relacionId)).thenReturn(Optional.empty());

        CreateInteraccionRequest request = CreateInteraccionRequest.builder()
                .tipo(Interaccion.TipoInteraccion.POSITIVA)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.register(relacionId, request, "tester"));

        assertEquals("Relacion not found: " + relacionId, ex.getMessage());
    }
}
