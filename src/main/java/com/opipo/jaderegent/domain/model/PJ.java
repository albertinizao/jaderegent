package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity

public class PJ {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID pjId;

    @Column(unique = true, nullable = false)
    private String nombreDisplay;

    private String notaOpcional;
    private String imagenUrl;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public PJ() {
    }

    public PJ(UUID pjId, String nombreDisplay, String notaOpcional, String imagenUrl, LocalDateTime fechaCreacion) {
        this.pjId = pjId;
        this.nombreDisplay = nombreDisplay;
        this.notaOpcional = notaOpcional;
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
    }

    public static PJBuilder builder() {
        return new PJBuilder();
    }

    public UUID getPjId() {
        return pjId;
    }

    public void setPjId(UUID pjId) {
        this.pjId = pjId;
    }

    public String getNombreDisplay() {
        return nombreDisplay;
    }

    public void setNombreDisplay(String nombreDisplay) {
        this.nombreDisplay = nombreDisplay;
    }

    public String getNotaOpcional() {
        return notaOpcional;
    }

    public void setNotaOpcional(String notaOpcional) {
        this.notaOpcional = notaOpcional;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public static class PJBuilder {
        private UUID pjId;
        private String nombreDisplay;
        private String notaOpcional;
        private String imagenUrl;
        private LocalDateTime fechaCreacion = LocalDateTime.now();

        PJBuilder() {
        }

        public PJBuilder pjId(UUID pjId) {
            this.pjId = pjId;
            return this;
        }

        public PJBuilder nombreDisplay(String nombreDisplay) {
            this.nombreDisplay = nombreDisplay;
            return this;
        }

        public PJBuilder notaOpcional(String notaOpcional) {
            this.notaOpcional = notaOpcional;
            return this;
        }

        public PJBuilder imagenUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
            return this;
        }

        public PJBuilder fechaCreacion(LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public PJ build() {
            return new PJ(pjId, nombreDisplay, notaOpcional, imagenUrl, fechaCreacion);
        }
    }
}
