package com.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rutina")
public class Rutina {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;
       
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @OneToMany(mappedBy = "rutina")
    @JsonIgnore
    private Set<Registro> registros;

    @OneToMany(mappedBy = "rutina")
    private Set<Item> items;

    @ManyToOne
    @JoinColumn(name = "profesional")
    @JsonIgnore
    private User profesional;

    @ManyToOne
    @JoinColumn(name = "paciente")
    @JsonIgnore
    private User paciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia")
    private Set<EDiaSemana> diasSemana = new HashSet<>();

    public Rutina() {
    }

    public Rutina(Set<Registro> registros, Set<Item> items, User profesional, User paciente) {
        this.registros = registros;
        this.items = items;
        this.profesional = profesional;
        this.paciente = paciente;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Set<Registro> getRegistros() {
        return registros;
    }

    public void setRegistros(Set<Registro> registros) {
        this.registros = registros;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public User getProfesional() {
        return profesional;
    }

    public void setProfesional(User profesional) {
        this.profesional = profesional;
    }

    public User getPaciente() {
        return paciente;
    }

    public void setPaciente(User paciente) {
        this.paciente = paciente;
    }

    public Set<EDiaSemana> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Set<EDiaSemana> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
