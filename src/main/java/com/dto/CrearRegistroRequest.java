package com.dto;

import java.util.Date;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CrearRegistroRequest {
    
    @NotNull
    private Date fecha;
    
    private String observaciones;
    
    @NotNull
    @Min(1)
    @Max(5)
    private Integer realizardo; // 1 al 5 escala de cumplimiento
    
    @NotNull
    @Min(1)
    @Max(5)
    private Integer intencidad; // 1 al 5 escala de intensidad
    
    @NotNull
    @Min(1)
    @Max(5)
    private Integer molestias; // 1 al 5 escala de molestias
    
    @NotNull
    private Long rutinaId;

    public CrearRegistroRequest() {
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getRealizardo() {
        return realizardo;
    }

    public void setRealizardo(Integer realizardo) {
        this.realizardo = realizardo;
    }

    public Integer getIntencidad() {
        return intencidad;
    }

    public void setIntencidad(Integer intencidad) {
        this.intencidad = intencidad;
    }

    public Integer getMolestias() {
        return molestias;
    }

    public void setMolestias(Integer molestias) {
        this.molestias = molestias;
    }

    public Long getRutinaId() {
        return rutinaId;
    }

    public void setRutinaId(Long rutinaId) {
        this.rutinaId = rutinaId;
    }
}
