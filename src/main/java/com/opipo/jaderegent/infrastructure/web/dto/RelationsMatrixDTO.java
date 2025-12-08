package com.opipo.jaderegent.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RelationsMatrixDTO {
    @JsonProperty("pjs")
    private List<PjSummaryDTO> pjs;

    @JsonProperty("npcs")
    private List<NpcSummaryDTO> npcs;

    @JsonProperty("matrix")
    private Map<String, MatrixCellDTO> matrix; // key: "pjId:npcId"

    public RelationsMatrixDTO() {
    }

    public RelationsMatrixDTO(List<PjSummaryDTO> pjs, List<NpcSummaryDTO> npcs,
            Map<String, MatrixCellDTO> matrix) {
        this.pjs = pjs;
        this.npcs = npcs;
        this.matrix = matrix;
    }

    public List<PjSummaryDTO> getPjs() {
        return pjs;
    }

    public void setPjs(List<PjSummaryDTO> pjs) {
        this.pjs = pjs;
    }

    public List<NpcSummaryDTO> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<NpcSummaryDTO> npcs) {
        this.npcs = npcs;
    }

    public Map<String, MatrixCellDTO> getMatrix() {
        return matrix;
    }

    public void setMatrix(Map<String, MatrixCellDTO> matrix) {
        this.matrix = matrix;
    }

    // Helper DTOs
    public static class PjSummaryDTO {
        @JsonProperty("pj_id")
        private UUID pjId;

        @JsonProperty("nombre_display")
        private String nombreDisplay;

        @JsonProperty("imagen_url")
        private String imagenUrl;

        public PjSummaryDTO() {
        }

        public PjSummaryDTO(UUID pjId, String nombreDisplay, String imagenUrl) {
            this.pjId = pjId;
            this.nombreDisplay = nombreDisplay;
            this.imagenUrl = imagenUrl;
        }

        public UUID getPjId() {
            return pjId;
        }

        public void setPjId(UUID pjId) {
            this.pjId = pjId;
        }

        public String getNombreDisplay() {
            return nombreDisplay;
        }

        public void setNombreDisplay(String nombreDisplay) {
            this.nombreDisplay = nombreDisplay;
        }

        public String getImagenUrl() {
            return imagenUrl;
        }

        public void setImagenUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
        }
    }

    public static class NpcSummaryDTO {
        @JsonProperty("npc_id")
        private String npcId;

        @JsonProperty("nombre")
        private String nombre;

        @JsonProperty("imagen_url")
        private String imagenUrl;

        @JsonProperty("nivel_maximo")
        private Integer nivelMaximo;

        public NpcSummaryDTO() {
        }

        public NpcSummaryDTO(String npcId, String nombre, String imagenUrl, Integer nivelMaximo) {
            this.npcId = npcId;
            this.nombre = nombre;
            this.imagenUrl = imagenUrl;
            this.nivelMaximo = nivelMaximo;
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
    }
}
