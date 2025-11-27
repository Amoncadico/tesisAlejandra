package com.dto;

import java.time.LocalDateTime;

public class MensajeDTO {
    private Long id;
    private Long foroId;
    private Long emisorId;
    private String emisorUsername;
    private String tipo;
    private String contenido;
    private String titulo;
    private String descripcion;
    private String iconoUrl;
    private String imagenFondo;
    private LocalDateTime fecha;

    public MensajeDTO() {
    }

    public MensajeDTO(Long id, Long foroId, Long emisorId, String emisorUsername, 
                      String tipo, String contenido, LocalDateTime fecha) {
        this.id = id;
        this.foroId = foroId;
        this.emisorId = emisorId;
        this.emisorUsername = emisorUsername;
        this.tipo = tipo;
        this.contenido = contenido;
        this.fecha = fecha;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getForoId() {
        return foroId;
    }

    public void setForoId(Long foroId) {
        this.foroId = foroId;
    }

    public Long getEmisorId() {
        return emisorId;
    }

    public void setEmisorId(Long emisorId) {
        this.emisorId = emisorId;
    }

    public String getEmisorUsername() {
        return emisorUsername;
    }

    public void setEmisorUsername(String emisorUsername) {
        this.emisorUsername = emisorUsername;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIconoUrl() {
        return iconoUrl;
    }

    public void setIconoUrl(String iconoUrl) {
        this.iconoUrl = iconoUrl;
    }

    public String getImagenFondo() {
        return imagenFondo;
    }

    public void setImagenFondo(String imagenFondo) {
        this.imagenFondo = imagenFondo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
