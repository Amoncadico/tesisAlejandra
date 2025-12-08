package com.dto;

import java.time.LocalDateTime;

public class LogroMensajeDTO {
    private Long id;
    private String tipo;
    private String contenido;
    private String titulo;
    private String descripcion;
    private String iconoUrl;
    private String imagenFondo;
    private LocalDateTime fecha;

    public LogroMensajeDTO(Long id, String tipo, String contenido, String titulo, String descripcion, String iconoUrl, String imagenFondo, LocalDateTime fecha) {
        this.id = id;
        this.tipo = tipo;
        this.contenido = contenido;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.iconoUrl = iconoUrl;
        this.imagenFondo = imagenFondo;
        this.fecha = fecha;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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