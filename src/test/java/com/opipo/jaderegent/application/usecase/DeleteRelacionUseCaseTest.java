package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteRelacionUseCaseTest {

    @Mock
    private RelacionRepository relacionRepository;

    @InjectMocks
    private DeleteRelacionUseCase useCase;

    @Test
    void delete_shouldRemoveRelationWhenAtLevelZeroAndNoInteractions() {
        UUID id = UUID.randomUUID();
        Relacion relacion = Relacion.builder().nivelActual(0).contadorInteracciones(0).build();
        when(relacionRepository.findById(id)).thenReturn(Optional.of(relacion));

        useCase.delete(id);

        verify(relacionRepository).deleteById(id);
    }

    @Test
    void delete_shouldFailWhenLevelIsNotZero() {
        UUID id = UUID.randomUUID();
        Relacion relacion = Relacion.builder().nivelActual(1).contadorInteracciones(0).build();
        when(relacionRepository.findById(id)).thenReturn(Optional.of(relacion));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> useCase.delete(id));

        assertEquals("Solo se pueden eliminar relaciones en nivel 0", ex.getMessage());
    }

    @Test
    void delete_shouldFailWhenInteractionsExist() {
        UUID id = UUID.randomUUID();
        Relacion relacion = Relacion.builder().nivelActual(0).contadorInteracciones(2).build();
        when(relacionRepository.findById(id)).thenReturn(Optional.of(relacion));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> useCase.delete(id));

        assertEquals("No se pueden eliminar relaciones con interacciones registradas", ex.getMessage());
    }

    @Test
    void delete_shouldFailWhenRelationNotFound() {
        UUID id = UUID.randomUUID();
        when(relacionRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.delete(id));

        assertEquals("Relación no encontrada", ex.getMessage());
    }
}
