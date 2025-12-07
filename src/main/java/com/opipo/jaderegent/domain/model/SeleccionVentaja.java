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
public class SeleccionVentaja {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "relacion_id")
    private Relacion relacion;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ventaja_id")
    private Ventaja ventaja;

    private Integer nivelAdquisicion;

    public SeleccionVentaja() {
    }

    public SeleccionVentaja(UUID id, Relacion relacion, Ventaja ventaja, Integer nivelAdquisicion) {
        this.id = id;
        this.relacion = relacion;
        this.ventaja = ventaja;
        this.nivelAdquisicion = nivelAdquisicion;
    }

    public static SeleccionVentajaBuilder builder() {
        return new SeleccionVentajaBuilder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Relacion getRelacion() {
        return relacion;
    }

    public void setRelacion(Relacion relacion) {
        this.relacion = relacion;
    }

    public Ventaja getVentaja() {
        return ventaja;
    }

    public void setVentaja(Ventaja ventaja) {
        this.ventaja = ventaja;
    }

    public Integer getNivelAdquisicion() {
        return nivelAdquisicion;
    }

    public void setNivelAdquisicion(Integer nivelAdquisicion) {
        this.nivelAdquisicion = nivelAdquisicion;
    }

    public static class SeleccionVentajaBuilder {
        private UUID id;
        private Relacion relacion;
        private Ventaja ventaja;
        private Integer nivelAdquisicion;

        SeleccionVentajaBuilder() {
        }

        public SeleccionVentajaBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public SeleccionVentajaBuilder relacion(Relacion relacion) {
            this.relacion = relacion;
            return this;
        }

        public SeleccionVentajaBuilder ventaja(Ventaja ventaja) {
            this.ventaja = ventaja;
            return this;
        }

        public SeleccionVentajaBuilder nivelAdquisicion(Integer nivelAdquisicion) {
            this.nivelAdquisicion = nivelAdquisicion;
            return this;
        }

        public SeleccionVentaja build() {
            return new SeleccionVentaja(id, relacion, ventaja, nivelAdquisicion);
        }
    }
}
