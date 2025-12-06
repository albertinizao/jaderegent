package com.opipo.jaderegent.infrastructure.web.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRelacionRequest {
    private UUID pjId;
    private String npcId;
}
