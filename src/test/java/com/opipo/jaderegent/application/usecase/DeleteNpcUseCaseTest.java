package com.opipo.jaderegent.application.usecase;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SnapshotSelecciones;
import com.opipo.jaderegent.domain.repository.InteraccionRepository;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.domain.repository.SeleccionVentajaRepository;
import com.opipo.jaderegent.domain.repository.SnapshotSeleccionItemRepository;
import com.opipo.jaderegent.domain.repository.SnapshotSeleccionesRepository;
import com.opipo.jaderegent.domain.repository.VentajaRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteNpcUseCaseTest {

    @Mock private NPCRepository npcRepository;
    @Mock private VentajaRepository ventajaRepository;
    @Mock private RelacionRepository relacionRepository;
    @Mock private InteraccionRepository interaccionRepository;
    @Mock private SeleccionVentajaRepository seleccionRepository;
    @Mock private SnapshotSeleccionesRepository snapshotRepository;
    @Mock private SnapshotSeleccionItemRepository snapshotItemRepository;

    @InjectMocks
    private DeleteNpcUseCase useCase;

    @Test
    void deleteNpc_shouldDeleteRelationsAdvantagesAndNpc() {
        String npcId = "npc-1";
        UUID relId = UUID.randomUUID();
        UUID snapId = UUID.randomUUID();

        Relacion relacion = Relacion.builder().build();
        relacion.setRelacionId(relId);
        SnapshotSelecciones snapshot = SnapshotSelecciones.builder().snapshotId(snapId).build();

        when(relacionRepository.findByNpcNpcId(npcId)).thenReturn(List.of(relacion));
        when(snapshotRepository.findByRelacionRelacionIdOrderByVersionDesc(relId)).thenReturn(List.of(snapshot));
        when(interaccionRepository.findByRelacionRelacionIdOrderByTsDesc(relId)).thenReturn(List.of());
        when(seleccionRepository.findByRelacionRelacionId(relId)).thenReturn(List.of());
        when(snapshotItemRepository.findBySnapshotSnapshotId(snapId)).thenReturn(List.of());
        when(ventajaRepository.findByNpcNpcId(npcId)).thenReturn(List.of());

        useCase.deleteNpc(npcId);

        verify(interaccionRepository).deleteAll(any());
        verify(seleccionRepository).deleteAll(any());
        verify(snapshotItemRepository).deleteAll(any());
        verify(snapshotRepository).deleteAll(any());
        verify(ventajaRepository).deleteAll(any());
        verify(relacionRepository).delete(relacion);
        verify(npcRepository).deleteById(npcId);
    }
}
