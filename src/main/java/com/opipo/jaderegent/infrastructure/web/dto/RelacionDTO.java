package com.opipo.jaderegent.infrastructure.web.dto;

import java.util.List;
import java.util.UUID;

public class RelacionDTO {
    private UUID relacionId;
    private String npcId;
    private String npcNombre;
    private String npcImagenUrl;
    private Integer nivelActual;
    private Integer nivelMaximo;
    private Boolean pendienteEleccion;
    private Boolean consistente;
    private Integer contadorInteracciones;
    private List<String> ventajasObtenidasIds;
    private List<SeleccionVentajaDTO> selecciones;
    private UUID pjId;
    private String pjNombre;
    private String pjImagenUrl;

    public RelacionDTO() {
    }

    public RelacionDTO(UUID relacionId, String npcId, String npcNombre, String npcImagenUrl,
            Integer nivelActual, Integer nivelMaximo, Boolean pendienteEleccion, Boolean consistente,
            Integer contadorInteracciones, List<String> ventajasObtenidasIds, List<SeleccionVentajaDTO> selecciones,
            UUID pjId, String pjNombre, String pjImagenUrl) {
        this.relacionId = relacionId;
        this.npcId = npcId;
        this.npcNombre = npcNombre;
        this.npcImagenUrl = npcImagenUrl;
        this.nivelActual = nivelActual;
        this.nivelMaximo = nivelMaximo;
        this.pendienteEleccion = pendienteEleccion;
        this.consistente = consistente;
        this.contadorInteracciones = contadorInteracciones;
        this.ventajasObtenidasIds = ventajasObtenidasIds;
        this.selecciones = selecciones;
        this.pjId = pjId;
        this.pjNombre = pjNombre;
        this.pjImagenUrl = pjImagenUrl;
    }

    public static RelacionDTOBuilder builder() {
        return new RelacionDTOBuilder();
    }

    public UUID getRelacionId() {
        return relacionId;
    }

    public void setRelacionId(UUID relacionId) {
        this.relacionId = relacionId;
    }

    public String getNpcId() {
        return npcId;
    }

    public void setNpcId(String npcId) {
        this.npcId = npcId;
    }

    public String getNpcNombre() {
        return npcNombre;
    }

    public void setNpcNombre(String npcNombre) {
        this.npcNombre = npcNombre;
    }

    public String getNpcImagenUrl() {
        return npcImagenUrl;
    }

    public void setNpcImagenUrl(String npcImagenUrl) {
        this.npcImagenUrl = npcImagenUrl;
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

    public Boolean getPendienteEleccion() {
        return pendienteEleccion;
    }

    public void setPendienteEleccion(Boolean pendienteEleccion) {
        this.pendienteEleccion = pendienteEleccion;
    }

    public Boolean getConsistente() {
        return consistente;
    }

    public void setConsistente(Boolean consistente) {
        this.consistente = consistente;
    }

    public Integer getContadorInteracciones() {
        return contadorInteracciones;
    }

    public void setContadorInteracciones(Integer contadorInteracciones) {
        this.contadorInteracciones = contadorInteracciones;
    }

    public List<String> getVentajasObtenidasIds() {
        return ventajasObtenidasIds;
    }

    public void setVentajasObtenidasIds(List<String> ventajasObtenidasIds) {
        this.ventajasObtenidasIds = ventajasObtenidasIds;
    }

    public List<SeleccionVentajaDTO> getSelecciones() {
        return selecciones;
    }

    public void setSelecciones(List<SeleccionVentajaDTO> selecciones) {
        this.selecciones = selecciones;
    }

    public UUID getPjId() {
        return pjId;
    }

    public void setPjId(UUID pjId) {
        this.pjId = pjId;
    }

    public String getPjNombre() {
        return pjNombre;
    }

    public void setPjNombre(String pjNombre) {
        this.pjNombre = pjNombre;
    }

    public String getPjImagenUrl() {
        return pjImagenUrl;
    }

    public void setPjImagenUrl(String pjImagenUrl) {
        this.pjImagenUrl = pjImagenUrl;
    }

    public static class RelacionDTOBuilder {
        private UUID relacionId;
        private String npcId;
        private String npcNombre;
        private String npcImagenUrl;
        private Integer nivelActual;
        private Integer nivelMaximo;
        private Boolean pendienteEleccion;
        private Boolean consistente;
        private Integer contadorInteracciones;
        private List<String> ventajasObtenidasIds;
        private List<SeleccionVentajaDTO> selecciones;
        private UUID pjId;
        private String pjNombre;
        private String pjImagenUrl;

        RelacionDTOBuilder() {
        }

        public RelacionDTOBuilder relacionId(UUID relacionId) {
            this.relacionId = relacionId;
            return this;
        }

        public RelacionDTOBuilder npcId(String npcId) {
            this.npcId = npcId;
            return this;
        }

        public RelacionDTOBuilder npcNombre(String npcNombre) {
            this.npcNombre = npcNombre;
            return this;
        }

        public RelacionDTOBuilder npcImagenUrl(String npcImagenUrl) {
            this.npcImagenUrl = npcImagenUrl;
            return this;
        }

        public RelacionDTOBuilder nivelActual(Integer nivelActual) {
            this.nivelActual = nivelActual;
            return this;
        }

        public RelacionDTOBuilder nivelMaximo(Integer nivelMaximo) {
            this.nivelMaximo = nivelMaximo;
            return this;
        }

        public RelacionDTOBuilder pendienteEleccion(Boolean pendienteEleccion) {
            this.pendienteEleccion = pendienteEleccion;
            return this;
        }

        public RelacionDTOBuilder consistente(Boolean consistente) {
            this.consistente = consistente;
            return this;
        }

        public RelacionDTOBuilder contadorInteracciones(Integer contadorInteracciones) {
            this.contadorInteracciones = contadorInteracciones;
            return this;
        }

        public RelacionDTOBuilder ventajasObtenidasIds(List<String> ventajasObtenidasIds) {
            this.ventajasObtenidasIds = ventajasObtenidasIds;
            return this;
        }

        public RelacionDTOBuilder selecciones(List<SeleccionVentajaDTO> selecciones) {
            this.selecciones = selecciones;
            return this;
        }

        public RelacionDTOBuilder pjId(UUID pjId) {
            this.pjId = pjId;
            return this;
        }

        public RelacionDTOBuilder pjNombre(String pjNombre) {
            this.pjNombre = pjNombre;
            return this;
        }

        public RelacionDTOBuilder pjImagenUrl(String pjImagenUrl) {
            this.pjImagenUrl = pjImagenUrl;
            return this;
        }

        public RelacionDTO build() {
            return new RelacionDTO(relacionId, npcId, npcNombre, npcImagenUrl, nivelActual, nivelMaximo,
                    pendienteEleccion, consistente, contadorInteracciones, ventajasObtenidasIds, selecciones,
                    pjId, pjNombre, pjImagenUrl);
        }
    }
}
