package com.opipo.jaderegent.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NpcDTO {
    @JsonProperty("npc_id")
    private String npcId;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("descripcion_larga")
    private String descripcionLarga;

    @JsonProperty("imagen_url")
    private String imagenUrl;

    @JsonProperty("nivel_maximo")
    private Integer nivelMaximo;

    public NpcDTO() {
    }

    public NpcDTO(String npcId, String nombre, String descripcionLarga, String imagenUrl, Integer nivelMaximo) {
        this.npcId = npcId;
        this.nombre = nombre;
        this.descripcionLarga = descripcionLarga;
        this.imagenUrl = imagenUrl;
        this.nivelMaximo = nivelMaximo;
    }

    public static NpcDTOBuilder builder() {
        return new NpcDTOBuilder();
    }

    public String getNpcId() {
        return npcId;
    }

    public void setNpcId(String npcId) {
        this.npcId = npcId;
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

    public Integer getNivelMaximo() {
        return nivelMaximo;
    }

    public void setNivelMaximo(Integer nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
    }

    public static class NpcDTOBuilder {
        private String npcId;
        private String nombre;
        private String descripcionLarga;
        private String imagenUrl;
        private Integer nivelMaximo;

        NpcDTOBuilder() {
        }

        public NpcDTOBuilder npcId(String npcId) {
            this.npcId = npcId;
            return this;
        }

        public NpcDTOBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public NpcDTOBuilder descripcionLarga(String descripcionLarga) {
            this.descripcionLarga = descripcionLarga;
            return this;
        }

        public NpcDTOBuilder imagenUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
            return this;
        }

        public NpcDTOBuilder nivelMaximo(Integer nivelMaximo) {
            this.nivelMaximo = nivelMaximo;
            return this;
        }

        public NpcDTO build() {
            return new NpcDTO(npcId, nombre, descripcionLarga, imagenUrl, nivelMaximo);
        }
    }
}
