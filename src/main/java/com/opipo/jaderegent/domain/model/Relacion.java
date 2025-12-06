package com.opipo.jaderegent.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

@Entity
public class Relacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID relacionId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pj_id")
    private PJ pj;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id")
    private NPC npc;

    private Integer nivelActual;
    private Boolean pendienteEleccion;
    private Boolean consistente;
    private Integer contadorInteracciones;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "relacion_ventajas", joinColumns = @JoinColumn(name = "relacion_id"), inverseJoinColumns = @JoinColumn(name = "ventaja_id"))
    private Set<Ventaja> ventajasObtenidas = new HashSet<>();

    private LocalDateTime ultimaActualizacionTs = LocalDateTime.now();

    public Relacion() {
    }

    public Relacion(UUID relacionId, PJ pj, NPC npc, Integer nivelActual, Boolean pendienteEleccion,
            Boolean consistente, Integer contadorInteracciones, Set<Ventaja> ventajasObtenidas,
            LocalDateTime ultimaActualizacionTs) {
        this.relacionId = relacionId;
        this.pj = pj;
        this.npc = npc;
        this.nivelActual = nivelActual;
        this.pendienteEleccion = pendienteEleccion;
        this.consistente = consistente;
        this.contadorInteracciones = contadorInteracciones;
        this.ventajasObtenidas = ventajasObtenidas != null ? ventajasObtenidas : new HashSet<>();
        this.ultimaActualizacionTs = ultimaActualizacionTs;
    }

    public static RelacionBuilder builder() {
        return new RelacionBuilder();
    }

    public UUID getRelacionId() {
        return relacionId;
    }

    public void setRelacionId(UUID relacionId) {
        this.relacionId = relacionId;
    }

    public PJ getPj() {
        return pj;
    }

    public void setPj(PJ pj) {
        this.pj = pj;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public Integer getNivelActual() {
        return nivelActual;
    }

    public void setNivelActual(Integer nivelActual) {
        this.nivelActual = nivelActual;
    }

    public Boolean getPendienteEleccion() {
        return pendienteEleccion;
    }

    public void setPendienteEleccion(Boolean pendienteEleccion) {
        this.pendienteEleccion = pendienteEleccion;
    }

    public Boolean getConsistente() {
        return consistente;
    }

    public void setConsistente(Boolean consistente) {
        this.consistente = consistente;
    }

    public Integer getContadorInteracciones() {
        return contadorInteracciones;
    }

    public void setContadorInteracciones(Integer contadorInteracciones) {
        this.contadorInteracciones = contadorInteracciones;
    }

    public LocalDateTime getUltimaActualizacionTs() {
        return ultimaActualizacionTs;
    }

    public void setUltimaActualizacionTs(LocalDateTime ultimaActualizacionTs) {
        this.ultimaActualizacionTs = ultimaActualizacionTs;
    }

    public Set<Ventaja> getVentajasObtenidas() {
        return ventajasObtenidas;
    }

    public void setVentajasObtenidas(Set<Ventaja> ventajasObtenidas) {
        this.ventajasObtenidas = ventajasObtenidas;
    }

    public static class RelacionBuilder {
        private UUID relacionId;
        private PJ pj;
        private NPC npc;
        private Integer nivelActual;
        private Boolean pendienteEleccion;
        private Boolean consistente;
        private Integer contadorInteracciones;
        private Set<Ventaja> ventajasObtenidas;
        private LocalDateTime ultimaActualizacionTs = LocalDateTime.now();

        RelacionBuilder() {
        }

        public RelacionBuilder pj(PJ pj) {
            this.pj = pj;
            return this;
        }

        public RelacionBuilder npc(NPC npc) {
            this.npc = npc;
            return this;
        }

        public RelacionBuilder nivelActual(Integer nivelActual) {
            this.nivelActual = nivelActual;
            return this;
        }

        public RelacionBuilder pendienteEleccion(Boolean pendienteEleccion) {
            this.pendienteEleccion = pendienteEleccion;
            return this;
        }

        public RelacionBuilder consistente(Boolean consistente) {
            this.consistente = consistente;
            return this;
        }

        public RelacionBuilder contadorInteracciones(Integer contadorInteracciones) {
            this.contadorInteracciones = contadorInteracciones;
            return this;
        }

        public RelacionBuilder ventajasObtenidas(Set<Ventaja> ventajasObtenidas) {
            this.ventajasObtenidas = ventajasObtenidas;
            return this;
        }

        public RelacionBuilder ultimaActualizacionTs(LocalDateTime ultimaActualizacionTs) {
            this.ultimaActualizacionTs = ultimaActualizacionTs;
            return this;
        }

        public Relacion build() {
            return new Relacion(relacionId, pj, npc, nivelActual, pendienteEleccion, consistente, contadorInteracciones,
                    ventajasObtenidas, ultimaActualizacionTs);
        }
    }
}
