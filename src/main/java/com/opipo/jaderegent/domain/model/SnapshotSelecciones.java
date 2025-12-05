package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class SnapshotSelecciones {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID snapshotId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "relacion_id")
    private Relacion relacion;

    private Integer version;

    private LocalDateTime ts = LocalDateTime.now();

    private String usuario;

    public SnapshotSelecciones() {
    }

    public SnapshotSelecciones(UUID snapshotId, Relacion relacion, Integer version, LocalDateTime ts, String usuario) {
        this.snapshotId = snapshotId;
        this.relacion = relacion;
        this.version = version;
        this.ts = ts;
        this.usuario = usuario;
    }

    public static SnapshotSeleccionesBuilder builder() {
        return new SnapshotSeleccionesBuilder();
    }

    public UUID getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(UUID snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Relacion getRelacion() {
        return relacion;
    }

    public void setRelacion(Relacion relacion) {
        this.relacion = relacion;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getTs() {
        return ts;
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public static class SnapshotSeleccionesBuilder {
        private UUID snapshotId;
        private Relacion relacion;
        private Integer version;
        private LocalDateTime ts = LocalDateTime.now();
        private String usuario;

        SnapshotSeleccionesBuilder() {
        }

        public SnapshotSeleccionesBuilder snapshotId(UUID snapshotId) {
            this.snapshotId = snapshotId;
            return this;
        }

        public SnapshotSeleccionesBuilder relacion(Relacion relacion) {
            this.relacion = relacion;
            return this;
        }

        public SnapshotSeleccionesBuilder version(Integer version) {
            this.version = version;
            return this;
        }

        public SnapshotSeleccionesBuilder ts(LocalDateTime ts) {
            this.ts = ts;
            return this;
        }

        public SnapshotSeleccionesBuilder usuario(String usuario) {
            this.usuario = usuario;
            return this;
        }

        public SnapshotSelecciones build() {
            return new SnapshotSelecciones(snapshotId, relacion, version, ts, usuario);
        }
    }
}
