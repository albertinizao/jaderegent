package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ventaja {
    @Id
    private String ventajaId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private NPC npc;

    @Column(nullable = false)
    private String nombre;

    @Lob
    private String descripcionLarga;

    private Integer minNivelRelacion;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> prerequisitos = new ArrayList<>();

    @Column
    private String prerequisitosOperator; // "AND" o "OR", por defecto "AND"

    public Ventaja() {
    }

    public Ventaja(String ventajaId, NPC npc, String nombre, String descripcionLarga, Integer minNivelRelacion,
            List<String> prerequisitos, String prerequisitosOperator) {
        this.ventajaId = ventajaId;
        this.npc = npc;
        this.nombre = nombre;
        this.descripcionLarga = descripcionLarga;
        this.minNivelRelacion = minNivelRelacion;
        this.prerequisitos = prerequisitos != null ? prerequisitos : new ArrayList<>();
        this.prerequisitosOperator = prerequisitosOperator != null ? prerequisitosOperator : "AND";
    }

    public static VentajaBuilder builder() {
        return new VentajaBuilder();
    }

    public String getVentajaId() {
        return ventajaId;
    }

    public void setVentajaId(String ventajaId) {
        this.ventajaId = ventajaId;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public void setDescripcionLarga(String descripcionLarga) {
        this.descripcionLarga = descripcionLarga;
    }

    public Integer getMinNivelRelacion() {
        return minNivelRelacion;
    }

    public void setMinNivelRelacion(Integer minNivelRelacion) {
        this.minNivelRelacion = minNivelRelacion;
    }

    public List<String> getPrerequisitos() {
        return prerequisitos;
    }

    public void setPrerequisitos(List<String> prerequisitos) {
        this.prerequisitos = prerequisitos;
    }

    public String getPrerequisitosOperator() {
        return prerequisitosOperator;
    }

    public void setPrerequisitosOperator(String prerequisitosOperator) {
        this.prerequisitosOperator = prerequisitosOperator;
    }

    public static class VentajaBuilder {
        private String ventajaId;
        private NPC npc;
        private String nombre;
        private String descripcionLarga;
        private Integer minNivelRelacion;
        private List<String> prerequisitos;
        private String prerequisitosOperator;

        VentajaBuilder() {
        }

        public VentajaBuilder ventajaId(String ventajaId) {
            this.ventajaId = ventajaId;
            return this;
        }

        public VentajaBuilder npc(NPC npc) {
            this.npc = npc;
            return this;
        }

        public VentajaBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public VentajaBuilder descripcionLarga(String descripcionLarga) {
            this.descripcionLarga = descripcionLarga;
            return this;
        }

        public VentajaBuilder minNivelRelacion(Integer minNivelRelacion) {
            this.minNivelRelacion = minNivelRelacion;
            return this;
        }

        public VentajaBuilder prerequisitos(List<String> prerequisitos) {
            this.prerequisitos = prerequisitos;
            return this;
        }

        public VentajaBuilder prerequisitosOperator(String prerequisitosOperator) {
            this.prerequisitosOperator = prerequisitosOperator;
            return this;
        }

        public Ventaja build() {
            return new Ventaja(ventajaId, npc, nombre, descripcionLarga, minNivelRelacion, prerequisitos,
                    prerequisitosOperator);
        }
    }
}
