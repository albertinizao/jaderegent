package com.opipo.jaderegent.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PjPrintableAdvantagesDTO {
    @JsonProperty("pj_nombre")
    private String pjNombre;

    @JsonProperty("pj_imagen_url")
    private String pjImagenUrl;

    @JsonProperty("npcs")
    private List<NpcVentajasDTO> npcs;

    public PjPrintableAdvantagesDTO() {
    }

    public PjPrintableAdvantagesDTO(String pjNombre, String pjImagenUrl, List<NpcVentajasDTO> npcs) {
        this.pjNombre = pjNombre;
        this.pjImagenUrl = pjImagenUrl;
        this.npcs = npcs;
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

    public List<NpcVentajasDTO> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<NpcVentajasDTO> npcs) {
        this.npcs = npcs;
    }

    public static class NpcVentajasDTO {
        @JsonProperty("npc_nombre")
        private String npcNombre;

        @JsonProperty("npc_imagen_url")
        private String npcImagenUrl;

        @JsonProperty("ventajas")
        private List<VentajaDetailDTO> ventajas;

        public NpcVentajasDTO() {
        }

        public NpcVentajasDTO(String npcNombre, String npcImagenUrl, List<VentajaDetailDTO> ventajas) {
            this.npcNombre = npcNombre;
            this.npcImagenUrl = npcImagenUrl;
            this.ventajas = ventajas;
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

        public List<VentajaDetailDTO> getVentajas() {
            return ventajas;
        }

        public void setVentajas(List<VentajaDetailDTO> ventajas) {
            this.ventajas = ventajas;
        }
    }

    public static class VentajaDetailDTO {
        @JsonProperty("nombre")
        private String nombre;

        @JsonProperty("descripcion")
        private String descripcion;

        public VentajaDetailDTO() {
        }

        public VentajaDetailDTO(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }
}
