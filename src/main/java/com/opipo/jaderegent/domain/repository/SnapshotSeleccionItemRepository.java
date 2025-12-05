package com.opipo.jaderegent.domain.repository;

import com.opipo.jaderegent.domain.model.SnapshotSeleccionItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotSeleccionItemRepository extends JpaRepository<SnapshotSeleccionItem, UUID> {
    List<SnapshotSeleccionItem> findBySnapshotSnapshotId(UUID snapshotId);
}
