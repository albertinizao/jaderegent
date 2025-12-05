package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;

@Entity
public class SnapshotSeleccionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id")
    private SnapshotSelecciones snapshot;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ventaja_id")
    private Ventaja ventaja;

    private Integer nivelRelacion;

    public SnapshotSeleccionItem() {
    }

    public SnapshotSeleccionItem(UUID id, SnapshotSelecciones snapshot, Ventaja ventaja, Integer nivelRelacion) {
        this.id = id;
        this.snapshot = snapshot;
        this.ventaja = ventaja;
        this.nivelRelacion = nivelRelacion;
    }

    public static SnapshotSeleccionItemBuilder builder() {
        return new SnapshotSeleccionItemBuilder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SnapshotSelecciones getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(SnapshotSelecciones snapshot) {
        this.snapshot = snapshot;
    }

    public Ventaja getVentaja() {
        return ventaja;
    }

    public void setVentaja(Ventaja ventaja) {
        this.ventaja = ventaja;
    }

    public Integer getNivelRelacion() {
        return nivelRelacion;
    }

    public void setNivelRelacion(Integer nivelRelacion) {
        this.nivelRelacion = nivelRelacion;
    }

    public static class SnapshotSeleccionItemBuilder {
        private UUID id;
        private SnapshotSelecciones snapshot;
        private Ventaja ventaja;
        private Integer nivelRelacion;

        SnapshotSeleccionItemBuilder() {
        }

        public SnapshotSeleccionItemBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public SnapshotSeleccionItemBuilder snapshot(SnapshotSelecciones snapshot) {
            this.snapshot = snapshot;
            return this;
        }

        public SnapshotSeleccionItemBuilder ventaja(Ventaja ventaja) {
            this.ventaja = ventaja;
            return this;
        }

        public SnapshotSeleccionItemBuilder nivelRelacion(Integer nivelRelacion) {
            this.nivelRelacion = nivelRelacion;
            return this;
        }

        public SnapshotSeleccionItem build() {
            return new SnapshotSeleccionItem(id, snapshot, ventaja, nivelRelacion);
        }
    }
}
