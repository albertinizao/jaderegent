package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SnapshotSelecciones;
import com.opipo.jaderegent.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeleteNpcUseCase {

    private final NPCRepository npcRepository;
    private final VentajaRepository ventajaRepository;
    private final RelacionRepository relacionRepository;
    private final InteraccionRepository interaccionRepository;
    private final SeleccionVentajaRepository seleccionRepository;
    private final SnapshotSeleccionesRepository snapshotRepository;
    private final SnapshotSeleccionItemRepository snapshotItemRepository;

    public DeleteNpcUseCase(NPCRepository npcRepository, VentajaRepository ventajaRepository,
            RelacionRepository relacionRepository, InteraccionRepository interaccionRepository,
            SeleccionVentajaRepository seleccionRepository, SnapshotSeleccionesRepository snapshotRepository,
            SnapshotSeleccionItemRepository snapshotItemRepository) {
        this.npcRepository = npcRepository;
        this.ventajaRepository = ventajaRepository;
        this.relacionRepository = relacionRepository;
        this.interaccionRepository = interaccionRepository;
        this.seleccionRepository = seleccionRepository;
        this.snapshotRepository = snapshotRepository;
        this.snapshotItemRepository = snapshotItemRepository;
    }

    public void deleteNpc(String npcId) {
        // 1. Find all relations
        List<Relacion> relaciones = relacionRepository.findByNpcNpcId(npcId);

        for (Relacion relacion : relaciones) {
            deleteRelacionData(relacion);
        }

        // 2. Delete advantages
        ventajaRepository.deleteAll(ventajaRepository.findByNpcNpcId(npcId));

        // 3. Delete NPC
        npcRepository.deleteById(npcId);
    }

    private void deleteRelacionData(Relacion relacion) {
        // Delete Interactions
        interaccionRepository
                .deleteAll(interaccionRepository.findByRelacionRelacionIdOrderByTsDesc(relacion.getRelacionId()));

        // Delete Selections
        seleccionRepository.deleteAll(seleccionRepository.findByRelacionRelacionId(relacion.getRelacionId()));

        // Delete Snapshots and Items
        List<SnapshotSelecciones> snapshots = snapshotRepository
                .findByRelacionRelacionIdOrderByVersionDesc(relacion.getRelacionId());
        for (SnapshotSelecciones snapshot : snapshots) {
            snapshotItemRepository.deleteAll(snapshotItemRepository.findBySnapshotSnapshotId(snapshot.getSnapshotId()));
        }
        snapshotRepository.deleteAll(snapshots);

        // Delete Relation
        relacionRepository.delete(relacion);
    }
}
