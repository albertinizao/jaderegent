package com.opipo.jaderegent.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opipo.jaderegent.domain.model.Interaccion.TipoInteraccion;

public class CreateInteraccionRequest {
    @JsonProperty("tipo")
    private TipoInteraccion tipo;

    @JsonProperty("nota")
    private String nota;

    // Constructors
    public CreateInteraccionRequest() {
    }

    public CreateInteraccionRequest(TipoInteraccion tipo, String nota) {
        this.tipo = tipo;
        this.nota = nota;
    }

    // Getters
    public TipoInteraccion getTipo() {
        return tipo;
    }

    public String getNota() {
        return nota;
    }

    // Setters
    public void setTipo(TipoInteraccion tipo) {
        this.tipo = tipo;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    // Builder pattern
    public static CreateInteraccionRequestBuilder builder() {
        return new CreateInteraccionRequestBuilder();
    }

    public static class CreateInteraccionRequestBuilder {
        private TipoInteraccion tipo;
        private String nota;

        public CreateInteraccionRequestBuilder tipo(TipoInteraccion tipo) {
            this.tipo = tipo;
            return this;
        }

        public CreateInteraccionRequestBuilder nota(String nota) {
            this.nota = nota;
            return this;
        }

        public CreateInteraccionRequest build() {
            return new CreateInteraccionRequest(tipo, nota);
        }
    }
}
