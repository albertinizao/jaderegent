package com.opipo.jaderegent.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class VentajaDTO {
    @JsonProperty("ventaja_id")
    private String ventajaId;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("descripcion_larga")
    private String descripcionLarga;

    @JsonProperty("min_nivel_relacion")
    private Integer minNivelRelacion;

    @JsonProperty("prerequisitos")
    private List<String> prerequisitos;

    @JsonProperty("prerequisitos_operator")
    private String prerequisitosOperator;

    public VentajaDTO() {
    }

    public VentajaDTO(String ventajaId, String nombre, String descripcionLarga, Integer minNivelRelacion,
            List<String> prerequisitos, String prerequisitosOperator) {
        this.ventajaId = ventajaId;
        this.nombre = nombre;
        this.descripcionLarga = descripcionLarga;
        this.minNivelRelacion = minNivelRelacion;
        this.prerequisitos = prerequisitos;
        this.prerequisitosOperator = prerequisitosOperator;
    }

    public static VentajaDTOBuilder builder() {
        return new VentajaDTOBuilder();
    }

    public String getVentajaId() {
        return ventajaId;
    }

    public void setVentajaId(String ventajaId) {
        this.ventajaId = ventajaId;
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

    public static class VentajaDTOBuilder {
        private String ventajaId;
        private String nombre;
        private String descripcionLarga;
        private Integer minNivelRelacion;
        private List<String> prerequisitos;
        private String prerequisitosOperator;

        VentajaDTOBuilder() {
        }

        public VentajaDTOBuilder ventajaId(String ventajaId) {
            this.ventajaId = ventajaId;
            return this;
        }

        public VentajaDTOBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public VentajaDTOBuilder descripcionLarga(String descripcionLarga) {
            this.descripcionLarga = descripcionLarga;
            return this;
        }

        public VentajaDTOBuilder minNivelRelacion(Integer minNivelRelacion) {
            this.minNivelRelacion = minNivelRelacion;
            return this;
        }

        public VentajaDTOBuilder prerequisitos(List<String> prerequisitos) {
            this.prerequisitos = prerequisitos;
            return this;
        }

        public VentajaDTOBuilder prerequisitosOperator(String prerequisitosOperator) {
            this.prerequisitosOperator = prerequisitosOperator;
            return this;
        }

        public VentajaDTO build() {
            return new VentajaDTO(ventajaId, nombre, descripcionLarga, minNivelRelacion, prerequisitos,
                    prerequisitosOperator);
        }
    }
}
