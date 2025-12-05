package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogAccion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID logId;

    @Builder.Default
    private LocalDateTime ts = LocalDateTime.now();
    
    private String usuario;
    private String accion;

    @Lob
    private String detalle;
}
