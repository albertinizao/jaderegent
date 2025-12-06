package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class NPC {
    @Id
    private String npcId;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Lob
    private String descripcionLarga;

    private Integer nivelMaximo;
    private String imagenUrl;
    private LocalDateTime fechaImportacion;

    @OneToMany(mappedBy = "npc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ventaja> ventajas = new ArrayList<>();

    public NPC() {
    }

    public NPC(String npcId, String nombre, String descripcionLarga, Integer nivelMaximo, String imagenUrl,
            LocalDateTime fechaImportacion, List<Ventaja> ventajas) {
        this.npcId = npcId;
        this.nombre = nombre;
        this.descripcionLarga = descripcionLarga;
        this.nivelMaximo = nivelMaximo;
        this.imagenUrl = imagenUrl;
        this.fechaImportacion = fechaImportacion;
        this.ventajas = ventajas != null ? ventajas : new ArrayList<>();
    }

    public static NPCBuilder builder() {
        return new NPCBuilder();
    }

    public String getNpcId() {
        return npcId;
    }

    public void setNpcId(String npcId) {
        this.npcId = npcId;
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

    public Integer getNivelMaximo() {
        return nivelMaximo;
    }

    public void setNivelMaximo(Integer nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public LocalDateTime getFechaImportacion() {
        return fechaImportacion;
    }

    public void setFechaImportacion(LocalDateTime fechaImportacion) {
        this.fechaImportacion = fechaImportacion;
    }

    public List<Ventaja> getVentajas() {
        return ventajas;
    }

    public void setVentajas(List<Ventaja> ventajas) {
        this.ventajas = ventajas;
    }

    public static class NPCBuilder {
        private String npcId;
        private String nombre;
        private String descripcionLarga;
        private Integer nivelMaximo;
        private String imagenUrl;
        private LocalDateTime fechaImportacion;
        private List<Ventaja> ventajas;

        NPCBuilder() {
        }

        public NPCBuilder npcId(String npcId) {
            this.npcId = npcId;
            return this;
        }

        public NPCBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public NPCBuilder descripcionLarga(String descripcionLarga) {
            this.descripcionLarga = descripcionLarga;
            return this;
        }

        public NPCBuilder nivelMaximo(Integer nivelMaximo) {
            this.nivelMaximo = nivelMaximo;
            return this;
        }

        public NPCBuilder imagenUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
            return this;
        }

        public NPCBuilder fechaImportacion(LocalDateTime fechaImportacion) {
            this.fechaImportacion = fechaImportacion;
            return this;
        }

        public NPC build() {
            return new NPC(npcId, nombre, descripcionLarga, nivelMaximo, imagenUrl, fechaImportacion, ventajas);
        }
    }
}
