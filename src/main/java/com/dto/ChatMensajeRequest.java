package com.dto;

public class ChatMensajeRequest {
    private Long foroId;
    private String contenido;
    private String tipo;

    public ChatMensajeRequest() {
    }

    public ChatMensajeRequest(Long foroId, String contenido, String tipo) {
        this.foroId = foroId;
        this.contenido = contenido;
        this.tipo = tipo;
    }

    // Getters y Setters
    public Long getForoId() {
        return foroId;
    }

    public void setForoId(Long foroId) {
        this.foroId = foroId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
