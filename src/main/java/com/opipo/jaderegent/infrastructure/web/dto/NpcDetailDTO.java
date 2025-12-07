package com.opipo.jaderegent.infrastructure.web.dto;

import java.util.List;

public class NpcDetailDTO {
    private NpcDTO npc;
    private List<VentajaDTO> ventajas;
    private List<RelacionDTO> relaciones;

    public NpcDetailDTO() {
    }

    public NpcDetailDTO(NpcDTO npc, List<VentajaDTO> ventajas, List<RelacionDTO> relaciones) {
        this.npc = npc;
        this.ventajas = ventajas;
        this.relaciones = relaciones;
    }

    public NpcDTO getNpc() {
        return npc;
    }

    public void setNpc(NpcDTO npc) {
        this.npc = npc;
    }

    public List<VentajaDTO> getVentajas() {
        return ventajas;
    }

    public void setVentajas(List<VentajaDTO> ventajas) {
        this.ventajas = ventajas;
    }

    public List<RelacionDTO> getRelaciones() {
        return relaciones;
    }

    public void setRelaciones(List<RelacionDTO> relaciones) {
        this.relaciones = relaciones;
    }
}
