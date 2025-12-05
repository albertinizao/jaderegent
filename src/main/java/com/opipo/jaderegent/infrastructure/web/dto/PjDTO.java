package com.opipo.jaderegent.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PjDTO {
    @JsonProperty("nombre_display")
    private String nombreDisplay;

    @JsonProperty("nota_opcional")
    private String notaOpcional;

    @JsonProperty("imagen_url")
    private String imagenUrl;

    public PjDTO() {
    }

    public PjDTO(String nombreDisplay, String notaOpcional, String imagenUrl) {
        this.nombreDisplay = nombreDisplay;
        this.notaOpcional = notaOpcional;
        this.imagenUrl = imagenUrl;
    }

    public static PjDTOBuilder builder() {
        return new PjDTOBuilder();
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
        private String nombreDisplay;
        private String notaOpcional;
        private String imagenUrl;

        PjDTOBuilder() {
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
            return new PjDTO(nombreDisplay, notaOpcional, imagenUrl);
        }
    }
}
