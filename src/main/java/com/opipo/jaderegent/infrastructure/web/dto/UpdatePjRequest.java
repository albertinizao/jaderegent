package com.opipo.jaderegent.infrastructure.web.dto;

public class UpdatePjRequest {
    private String nombreDisplay;
    private String notaOpcional;
    private String imagenUrl;

    public UpdatePjRequest() {
    }

    public UpdatePjRequest(String nombreDisplay, String notaOpcional, String imagenUrl) {
        this.nombreDisplay = nombreDisplay;
        this.notaOpcional = notaOpcional;
        this.imagenUrl = imagenUrl;
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
}
