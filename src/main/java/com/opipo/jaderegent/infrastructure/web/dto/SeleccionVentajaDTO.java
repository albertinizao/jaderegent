package com.opipo.jaderegent.infrastructure.web.dto;

public class SeleccionVentajaDTO {
    private String ventajaId;
    private String nombre;
    private Integer nivelAdquisicion;
    private Integer nivelVentaja;
    private String descripcion;

    public SeleccionVentajaDTO() {
    }

    public SeleccionVentajaDTO(String ventajaId, String nombre, Integer nivelAdquisicion, Integer nivelVentaja,
            String descripcion) {
        this.ventajaId = ventajaId;
        this.nombre = nombre;
        this.nivelAdquisicion = nivelAdquisicion;
        this.nivelVentaja = nivelVentaja;
        this.descripcion = descripcion;
    }

    public static SeleccionVentajaDTOBuilder builder() {
        return new SeleccionVentajaDTOBuilder();
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

    public Integer getNivelAdquisicion() {
        return nivelAdquisicion;
    }

    public void setNivelAdquisicion(Integer nivelAdquisicion) {
        this.nivelAdquisicion = nivelAdquisicion;
    }

    public Integer getNivelVentaja() {
        return nivelVentaja;
    }

    public void setNivelVentaja(Integer nivelVentaja) {
        this.nivelVentaja = nivelVentaja;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public static class SeleccionVentajaDTOBuilder {
        private String ventajaId;
        private String nombre;
        private Integer nivelAdquisicion;
        private Integer nivelVentaja;
        private String descripcion;

        SeleccionVentajaDTOBuilder() {
        }

        public SeleccionVentajaDTOBuilder ventajaId(String ventajaId) {
            this.ventajaId = ventajaId;
            return this;
        }

        public SeleccionVentajaDTOBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public SeleccionVentajaDTOBuilder nivelAdquisicion(Integer nivelAdquisicion) {
            this.nivelAdquisicion = nivelAdquisicion;
            return this;
        }

        public SeleccionVentajaDTOBuilder nivelVentaja(Integer nivelVentaja) {
            this.nivelVentaja = nivelVentaja;
            return this;
        }

        public SeleccionVentajaDTOBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public SeleccionVentajaDTO build() {
            return new SeleccionVentajaDTO(ventajaId, nombre, nivelAdquisicion, nivelVentaja, descripcion);
        }
    }
}
