package com.dto;

import java.util.Date;

public class RegistroResponseDTO {
    
    private Long id;
    private Date fecha;
    private String observaciones;
    private Integer realizardo;
    private Integer intencidad;
    private Integer molestias;
    private Long rutinaId;
    private String rutinaNombre;
    private String pacienteNombre;
    private String pacienteFoto;

    public RegistroResponseDTO() {
    }

    public RegistroResponseDTO(Long id, Date fecha, String observaciones, Integer realizardo, 
                              Integer intencidad, Integer molestias, Long rutinaId, String rutinaNombre,
                              String pacienteNombre, String pacienteFoto) {
        this.id = id;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.realizardo = realizardo;
        this.intencidad = intencidad;
        this.molestias = molestias;
        this.rutinaId = rutinaId;
        this.rutinaNombre = rutinaNombre;
        this.pacienteNombre = pacienteNombre;
        this.pacienteFoto = pacienteFoto;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRutinaNombre() {
        return rutinaNombre;
    }

    public void setRutinaNombre(String rutinaNombre) {
        this.rutinaNombre = rutinaNombre;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre;
    }

    public String getPacienteFoto() {
        return pacienteFoto;
    }

    public void setPacienteFoto(String pacienteFoto) {
        this.pacienteFoto = pacienteFoto;
    }
}
