package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SnapshotSelecciones;
import com.opipo.jaderegent.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DeletePjUseCase {

    private final PJRepository pjRepository;
    private final RelacionRepository relacionRepository;
    private final InteraccionRepository interaccionRepository;
    private final SeleccionVentajaRepository seleccionRepository;
    private final SnapshotSeleccionesRepository snapshotRepository;
    private final SnapshotSeleccionItemRepository snapshotItemRepository;

    public DeletePjUseCase(PJRepository pjRepository, RelacionRepository relacionRepository,
            InteraccionRepository interaccionRepository, SeleccionVentajaRepository seleccionRepository,
            SnapshotSeleccionesRepository snapshotRepository, SnapshotSeleccionItemRepository snapshotItemRepository) {
        this.pjRepository = pjRepository;
        this.relacionRepository = relacionRepository;
        this.interaccionRepository = interaccionRepository;
        this.seleccionRepository = seleccionRepository;
        this.snapshotRepository = snapshotRepository;
        this.snapshotItemRepository = snapshotItemRepository;
    }

    public void deletePj(UUID pjId) {
        // 1. Find all relations
        List<Relacion> relaciones = relacionRepository.findByPjPjId(pjId);

        for (Relacion relacion : relaciones) {
            deleteRelacionData(relacion);
        }

        // 2. Delete PJ
        pjRepository.deleteById(pjId);
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
