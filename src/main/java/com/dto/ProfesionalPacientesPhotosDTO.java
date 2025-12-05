package com.dto;

import java.util.List;

public class ProfesionalPacientesPhotosDTO {
    private UserPhotoDTO profesional;
    private List<UserPhotoDTO> pacientes;

    public ProfesionalPacientesPhotosDTO() {
    }

    public ProfesionalPacientesPhotosDTO(UserPhotoDTO profesional, List<UserPhotoDTO> pacientes) {
        this.profesional = profesional;
        this.pacientes = pacientes;
    }

    public UserPhotoDTO getProfesional() {
        return profesional;
    }

    public void setProfesional(UserPhotoDTO profesional) {
        this.profesional = profesional;
    }

    public List<UserPhotoDTO> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<UserPhotoDTO> pacientes) {
        this.pacientes = pacientes;
    }
}
