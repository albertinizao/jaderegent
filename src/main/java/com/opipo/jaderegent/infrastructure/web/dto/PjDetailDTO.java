package com.opipo.jaderegent.infrastructure.web.dto;

import java.util.List;

public class PjDetailDTO {
    private PjDTO pj;
    private List<RelacionDTO> relaciones;

    public PjDetailDTO() {
    }

    public PjDetailDTO(PjDTO pj, List<RelacionDTO> relaciones) {
        this.pj = pj;
        this.relaciones = relaciones;
    }

    public static PjDetailDTOBuilder builder() {
        return new PjDetailDTOBuilder();
    }

    public PjDTO getPj() {
        return pj;
    }

    public void setPj(PjDTO pj) {
        this.pj = pj;
    }

    public List<RelacionDTO> getRelaciones() {
        return relaciones;
    }

    public void setRelaciones(List<RelacionDTO> relaciones) {
        this.relaciones = relaciones;
    }

    public static class PjDetailDTOBuilder {
        private PjDTO pj;
        private List<RelacionDTO> relaciones;

        PjDetailDTOBuilder() {
        }

        public PjDetailDTOBuilder pj(PjDTO pj) {
            this.pj = pj;
            return this;
        }

        public PjDetailDTOBuilder relaciones(List<RelacionDTO> relaciones) {
            this.relaciones = relaciones;
            return this;
        }

        public PjDetailDTO build() {
            return new PjDetailDTO(pj, relaciones);
        }
    }
}
