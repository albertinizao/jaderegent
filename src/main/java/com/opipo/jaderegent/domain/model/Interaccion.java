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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
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

    private LocalDateTime ts = LocalDateTime.now();

    private String usuario;

    public Interaccion() {
    }

    public Interaccion(UUID interaccionId, Relacion relacion, TipoInteraccion tipo, Integer valor, String nota,
            LocalDateTime ts, String usuario) {
        this.interaccionId = interaccionId;
        this.relacion = relacion;
        this.tipo = tipo;
        this.valor = valor;
        this.nota = nota;
        this.ts = ts != null ? ts : LocalDateTime.now();
        this.usuario = usuario;
    }

    public static InteraccionBuilder builder() {
        return new InteraccionBuilder();
    }

    public UUID getInteraccionId() {
        return interaccionId;
    }

    public void setInteraccionId(UUID interaccionId) {
        this.interaccionId = interaccionId;
    }

    public Relacion getRelacion() {
        return relacion;
    }

    public void setRelacion(Relacion relacion) {
        this.relacion = relacion;
    }

    public TipoInteraccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoInteraccion tipo) {
        this.tipo = tipo;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public LocalDateTime getTs() {
        return ts;
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public static class InteraccionBuilder {
        private UUID interaccionId;
        private Relacion relacion;
        private TipoInteraccion tipo;
        private Integer valor;
        private String nota;
        private LocalDateTime ts = LocalDateTime.now();
        private String usuario;

        InteraccionBuilder() {
        }

        public InteraccionBuilder interaccionId(UUID interaccionId) {
            this.interaccionId = interaccionId;
            return this;
        }

        public InteraccionBuilder relacion(Relacion relacion) {
            this.relacion = relacion;
            return this;
        }

        public InteraccionBuilder tipo(TipoInteraccion tipo) {
            this.tipo = tipo;
            return this;
        }

        public InteraccionBuilder valor(Integer valor) {
            this.valor = valor;
            return this;
        }

        public InteraccionBuilder nota(String nota) {
            this.nota = nota;
            return this;
        }

        public InteraccionBuilder ts(LocalDateTime ts) {
            this.ts = ts;
            return this;
        }

        public InteraccionBuilder usuario(String usuario) {
            this.usuario = usuario;
            return this;
        }

        public Interaccion build() {
            return new Interaccion(interaccionId, relacion, tipo, valor, nota, ts, usuario);
        }
    }
}
