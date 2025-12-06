package com.opipo.jaderegent.infrastructure.web.dto;

public class UpdateNpcRequest {
    private String nombre;
    private String descripcionLarga;
    private String imagenUrl;

    public UpdateNpcRequest() {
    }

    public UpdateNpcRequest(String nombre, String descripcionLarga, String imagenUrl) {
        this.nombre = nombre;
        this.descripcionLarga = descripcionLarga;
        this.imagenUrl = imagenUrl;
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

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
