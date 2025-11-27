package com.dto;

public class ForoDTO {
    private Long id;
    private Long profesionalId;
    private String profesionalUsername;
    private Long pacienteId;
    private String pacienteUsername;
    private MensajeDTO ultimoMensaje;

    public ForoDTO() {
    }

    public ForoDTO(Long id, Long profesionalId, String profesionalUsername,
                   Long pacienteId, String pacienteUsername) {
        this.id = id;
        this.profesionalId = profesionalId;
        this.profesionalUsername = profesionalUsername;
        this.pacienteId = pacienteId;
        this.pacienteUsername = pacienteUsername;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfesionalId() {
        return profesionalId;
    }

    public void setProfesionalId(Long profesionalId) {
        this.profesionalId = profesionalId;
    }

    public String getProfesionalUsername() {
        return profesionalUsername;
    }

    public void setProfesionalUsername(String profesionalUsername) {
        this.profesionalUsername = profesionalUsername;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPacienteUsername() {
        return pacienteUsername;
    }

    public void setPacienteUsername(String pacienteUsername) {
        this.pacienteUsername = pacienteUsername;
    }

    public MensajeDTO getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(MensajeDTO ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }
}
