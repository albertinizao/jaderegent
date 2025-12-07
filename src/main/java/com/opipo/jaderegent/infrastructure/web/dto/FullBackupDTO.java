package com.opipo.jaderegent.infrastructure.web.dto;

import java.util.List;

public class FullBackupDTO {
    private List<PjDetailDTO> pjs;
    private List<NpcDetailDTO> npcs;

    public FullBackupDTO() {
    }

    public FullBackupDTO(List<PjDetailDTO> pjs, List<NpcDetailDTO> npcs) {
        this.pjs = pjs;
        this.npcs = npcs;
    }

    public List<PjDetailDTO> getPjs() {
        return pjs;
    }

    public void setPjs(List<PjDetailDTO> pjs) {
        this.pjs = pjs;
    }

    public List<NpcDetailDTO> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<NpcDetailDTO> npcs) {
        this.npcs = npcs;
    }
}
