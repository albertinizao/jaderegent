package com.opipo.jaderegent.application.usecase;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SnapshotSelecciones;
import com.opipo.jaderegent.domain.repository.InteraccionRepository;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.domain.repository.SeleccionVentajaRepository;
import com.opipo.jaderegent.domain.repository.SnapshotSeleccionItemRepository;
import com.opipo.jaderegent.domain.repository.SnapshotSeleccionesRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeletePjUseCaseTest {

    @Mock private PJRepository pjRepository;
    @Mock private RelacionRepository relacionRepository;
    @Mock private InteraccionRepository interaccionRepository;
    @Mock private SeleccionVentajaRepository seleccionRepository;
    @Mock private SnapshotSeleccionesRepository snapshotRepository;
    @Mock private SnapshotSeleccionItemRepository snapshotItemRepository;

    @InjectMocks
    private DeletePjUseCase useCase;

    @Test
    void deletePj_shouldDeleteRelatedDataAndThenPj() {
        UUID pjId = UUID.randomUUID();
        UUID relId = UUID.randomUUID();
        UUID snapId = UUID.randomUUID();

        Relacion relacion = Relacion.builder().build();
        relacion.setRelacionId(relId);
        SnapshotSelecciones snapshot = SnapshotSelecciones.builder().snapshotId(snapId).build();

        when(relacionRepository.findByPjPjId(pjId)).thenReturn(List.of(relacion));
        when(snapshotRepository.findByRelacionRelacionIdOrderByVersionDesc(relId)).thenReturn(List.of(snapshot));
        when(interaccionRepository.findByRelacionRelacionIdOrderByTsDesc(relId)).thenReturn(List.of());
        when(seleccionRepository.findByRelacionRelacionId(relId)).thenReturn(List.of());
        when(snapshotItemRepository.findBySnapshotSnapshotId(snapId)).thenReturn(List.of());

        useCase.deletePj(pjId);

        verify(interaccionRepository).deleteAll(any());
        verify(seleccionRepository).deleteAll(any());
        verify(snapshotItemRepository).deleteAll(any());
        verify(snapshotRepository).deleteAll(any());
        verify(relacionRepository, times(1)).delete(relacion);
        verify(pjRepository).deleteById(pjId);
    }
}
