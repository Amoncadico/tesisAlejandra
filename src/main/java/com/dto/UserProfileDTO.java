package com.dto;

import java.time.LocalDate;

public class UserProfileDTO {
    private Long id;
    private String username;
    private String rut;
    private LocalDate fechaNacimiento;
    private String lesion;
    private LocalDate fechaRegistro;
    private String foto;
    private String profesionalAsignado; // username del profesional
    private Integer cantidadPacientes;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Long id, String username, String rut, LocalDate fechaNacimiento,
                          String lesion, LocalDate fechaRegistro, String foto, String profesionalAsignado, Integer cantidadPacientes) {
        this.id = id;
        this.username = username;
        this.rut = rut;
        this.fechaNacimiento = fechaNacimiento;
        this.lesion = lesion;
        this.fechaRegistro = fechaRegistro;
        this.foto = foto;
        this.profesionalAsignado = profesionalAsignado;
        this.cantidadPacientes = cantidadPacientes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getLesion() {
        return lesion;
    }

    public void setLesion(String lesion) {
        this.lesion = lesion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getProfesionalAsignado() {
        return profesionalAsignado;
    }

    public void setProfesionalAsignado(String profesionalAsignado) {
        this.profesionalAsignado = profesionalAsignado;
    }

    public Integer getCantidadPacientes() {
        return cantidadPacientes;
    }

    public void setCantidadPacientes(Integer cantidadPacientes) {
        this.cantidadPacientes = cantidadPacientes;
    }
}
