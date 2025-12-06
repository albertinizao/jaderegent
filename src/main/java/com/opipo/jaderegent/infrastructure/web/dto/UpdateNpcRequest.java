package com.opipo.jaderegent.infrastructure.web.dto;

public class UpdateNpcRequest {
    private String descripcionLarga;
    private String imagenUrl;

    public UpdateNpcRequest() {
    }

    public UpdateNpcRequest(String descripcionLarga, String imagenUrl) {
        this.descripcionLarga = descripcionLarga;
        this.imagenUrl = imagenUrl;
    }

    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public void setDescripcionLarga(String descripcionLarga) {
        this.descripcionLarga = descripcionLarga;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
