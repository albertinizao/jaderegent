package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interaccion {
    public enum TipoInteraccion {
        POSITIVA, NEGATIVA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID interaccionId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "relacion_id")
    @JsonIgnore
    private Relacion relacion;

    @Enumerated(EnumType.STRING)
    private TipoInteraccion tipo;

    private Integer valor;
    private String nota;

    @Builder.Default
    private LocalDateTime ts = LocalDateTime.now();

    private String usuario;
}
