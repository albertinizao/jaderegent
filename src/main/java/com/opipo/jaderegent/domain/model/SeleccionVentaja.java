package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class SeleccionVentaja {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID seleccionId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "relacion_id")
    private Relacion relacion;

    private Integer nivelRelacion;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ventaja_id")
    private Ventaja ventaja;

    @Builder.Default
    private LocalDateTime ts = LocalDateTime.now();
    
    private String usuario;
}
