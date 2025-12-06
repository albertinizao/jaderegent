package com.opipo.jaderegent.infrastructure.web.dto;

import java.util.UUID;

public class CreateRelacionRequest {
    private UUID pjId;
    private String npcId;

    public CreateRelacionRequest() {
    }

    public CreateRelacionRequest(UUID pjId, String npcId) {
        this.pjId = pjId;
        this.npcId = npcId;
    }

    public UUID getPjId() {
        return pjId;
    }

    public void setPjId(UUID pjId) {
        this.pjId = pjId;
    }

    public String getNpcId() {
        return npcId;
    }

    public void setNpcId(String npcId) {
        this.npcId = npcId;
    }

    public static CreateRelacionRequestBuilder builder() {
        return new CreateRelacionRequestBuilder();
    }

    public static class CreateRelacionRequestBuilder {
        private UUID pjId;
        private String npcId;

        CreateRelacionRequestBuilder() {
        }

        public CreateRelacionRequestBuilder pjId(UUID pjId) {
            this.pjId = pjId;
            return this;
        }

        public CreateRelacionRequestBuilder npcId(String npcId) {
            this.npcId = npcId;
            return this;
        }

        public CreateRelacionRequest build() {
            return new CreateRelacionRequest(pjId, npcId);
        }
    }
}
