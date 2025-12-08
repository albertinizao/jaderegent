package com.opipo.jaderegent.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class MatrixCellDTO {
    @JsonProperty("relacion_id")
    private UUID relacionId;

    @JsonProperty("nivel_actual")
    private Integer nivelActual;

    @JsonProperty("nivel_maximo")
    private Integer nivelMaximo;

    @JsonProperty("contador_interacciones")
    private Integer contadorInteracciones;

    @JsonProperty("existe_relacion")
    private Boolean existeRelacion;

    public MatrixCellDTO() {
    }

    public MatrixCellDTO(UUID relacionId, Integer nivelActual, Integer nivelMaximo,
            Integer contadorInteracciones, Boolean existeRelacion) {
        this.relacionId = relacionId;
        this.nivelActual = nivelActual;
        this.nivelMaximo = nivelMaximo;
        this.contadorInteracciones = contadorInteracciones;
        this.existeRelacion = existeRelacion;
    }

    public static MatrixCellDTO empty() {
        return new MatrixCellDTO(null, 0, 0, 0, false);
    }

    public static MatrixCellDTO fromRelacion(UUID relacionId, Integer nivelActual,
            Integer nivelMaximo, Integer contadorInteracciones) {
        return new MatrixCellDTO(relacionId, nivelActual, nivelMaximo, contadorInteracciones, true);
    }

    public UUID getRelacionId() {
        return relacionId;
    }

    public void setRelacionId(UUID relacionId) {
        this.relacionId = relacionId;
    }

    public Integer getNivelActual() {
        return nivelActual;
    }

    public void setNivelActual(Integer nivelActual) {
        this.nivelActual = nivelActual;
    }

    public Integer getNivelMaximo() {
        return nivelMaximo;
    }

    public void setNivelMaximo(Integer nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
    }

    public Integer getContadorInteracciones() {
        return contadorInteracciones;
    }

    public void setContadorInteracciones(Integer contadorInteracciones) {
        this.contadorInteracciones = contadorInteracciones;
    }

    public Boolean getExisteRelacion() {
        return existeRelacion;
    }

    public void setExisteRelacion(Boolean existeRelacion) {
        this.existeRelacion = existeRelacion;
    }
}
