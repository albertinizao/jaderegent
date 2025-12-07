package com.opipo.jaderegent.infrastructure.web.dto;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.Ventaja;
import java.util.List;

public class NpcDetailDTO {
    private NPC npc;
    private List<Ventaja> ventajas;
    private List<RelacionDTO> relaciones;

    public NpcDetailDTO(NPC npc, List<Ventaja> ventajas, List<RelacionDTO> relaciones) {
        this.npc = npc;
        this.ventajas = ventajas;
        this.relaciones = relaciones;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public List<Ventaja> getVentajas() {
        return ventajas;
    }

    public void setVentajas(List<Ventaja> ventajas) {
        this.ventajas = ventajas;
    }

    public List<RelacionDTO> getRelaciones() {
        return relaciones;
    }

    public void setRelaciones(List<RelacionDTO> relaciones) {
        this.relaciones = relaciones;
    }
}
