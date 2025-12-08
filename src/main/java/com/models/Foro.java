package com.models;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "foro")
public class Foro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profesional_id")
    private User profesional;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private User paciente;

    private boolean logroImpulso;

    private boolean logroRitmo;

    private boolean logroDominio;

    @OneToMany(mappedBy = "foro", cascade = CascadeType.ALL)
    private Set<Mensaje> mensajes;

    public Foro() {}

    public Foro(User profesional, User paciente) {
        this.profesional = profesional;
        this.paciente = paciente;
        this.logroImpulso = false;
        this.logroRitmo = false;
        this.logroDominio = false;
    }

    public Long getId() {
        return id;
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

    public boolean isLogroImpulso() {
        return logroImpulso;
    }

    public boolean isLogroRitmo() {
        return logroRitmo;
    }

    public boolean isLogroDominio() {
        return logroDominio;
    }

    public void setLogroImpulso(boolean logroImpulso) {
        this.logroImpulso = logroImpulso;
    }

    public void setLogroRitmo(boolean logroRitmo) {
        this.logroRitmo = logroRitmo;
    }

    public void setLogroDominio(boolean logroDominio) {
        this.logroDominio = logroDominio;
    }

    public Set<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(Set<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }
}
