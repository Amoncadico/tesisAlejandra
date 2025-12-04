package com.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registro")
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "realizardo")
    private int realizardo; // 1 al 5 escala de cumplimiento

    @Column(name = "intencidad")
    private int intencidad; // 1 al 5 escala de intensidad

    @Column(name = "molestias")
    private int molestias; // 1 al 5 escala de molestias

    @ManyToOne
    @JoinColumn(name = "rutina")
    @JsonIgnore
    private Rutina rutina;

    public Registro() {
    }

    public Registro(Date fecha, String observaciones, int realizardo, int intencidad, int molestias, Rutina rutina) {
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.realizardo = realizardo;
        this.intencidad = intencidad;
        this.molestias = molestias;
        this.rutina = rutina;
    }

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

    public int getRealizardo() {
        return realizardo;
    }

    public void setRealizardo(int realizardo) {
        this.realizardo = realizardo;
    }

    public int getIntencidad() {
        return intencidad;
    }

    public void setIntencidad(int intencidad) {
        this.intencidad = intencidad;
    }

    public int getMolestias() {
        return molestias;
    }

    public void setMolestias(int molestias) {
        this.molestias = molestias;
    }

    public Rutina getRutina() {
        return rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }

}
