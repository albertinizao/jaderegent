package com.opipo.jaderegent.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PjDTO {
    @JsonProperty("pj_id")
    private java.util.UUID pjId;

    @JsonProperty("nombre_display")
    private String nombreDisplay;

    @JsonProperty("nota_opcional")
    private String notaOpcional;

    @JsonProperty("imagen_url")
    private String imagenUrl;

    public PjDTO() {
    }

    public PjDTO(java.util.UUID pjId, String nombreDisplay, String notaOpcional, String imagenUrl) {
        this.pjId = pjId;
        this.nombreDisplay = nombreDisplay;
        this.notaOpcional = notaOpcional;
        this.imagenUrl = imagenUrl;
    }

    public static PjDTOBuilder builder() {
        return new PjDTOBuilder();
    }

    public java.util.UUID getPjId() {
        return pjId;
    }

    public void setPjId(java.util.UUID pjId) {
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

    public static class PjDTOBuilder {
        private java.util.UUID pjId;
        private String nombreDisplay;
        private String notaOpcional;
        private String imagenUrl;

        PjDTOBuilder() {
        }

        public PjDTOBuilder pjId(java.util.UUID pjId) {
            this.pjId = pjId;
            return this;
        }

        public PjDTOBuilder nombreDisplay(String nombreDisplay) {
            this.nombreDisplay = nombreDisplay;
            return this;
        }

        public PjDTOBuilder notaOpcional(String notaOpcional) {
            this.notaOpcional = notaOpcional;
            return this;
        }

        public PjDTOBuilder imagenUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
            return this;
        }

        public PjDTO build() {
            return new PjDTO(pjId, nombreDisplay, notaOpcional, imagenUrl);
        }
    }
}
