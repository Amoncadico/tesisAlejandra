package com.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username")
       })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 120)
  private String password;

  @Column(name = "rut")
  private String rut;

  @Column(name = "lesion")
  private String lesion;
  
  @Column(name = "fecha_nacimiento")
  private LocalDate fechaNacimiento;
  
  @Column(name = "fecha_registro")
  private LocalDate fechaRegistro;

  @Column(name = "foto")
  private String foto;

  @OneToMany(mappedBy = "profesional")
  private Set<Rutina> rutinasCreadas;

  @OneToMany(mappedBy = "paciente")
  private Set<Rutina> rutinasAsignadas;


  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles",
             joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profesional_id")
  private User profesionalAsignado;

  @OneToMany(mappedBy = "profesionalAsignado", fetch = FetchType.LAZY)
  private Set<User> pacientesAsignados = new HashSet<>();

  public User() {
  }  public User(String username, String password) {
    this.username = username;
    this.password = password;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRut() {
    return rut;
  }

  public void setRut(String rut) {
    this.rut = rut;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public User getProfesionalAsignado() {
    return profesionalAsignado;
  }

  public void setProfesionalAsignado(User profesionalAsignado) {
    this.profesionalAsignado = profesionalAsignado;
  }

  public Set<User> getPacientesAsignados() {
    return pacientesAsignados;
  }

  public void setPacientesAsignados(Set<User> pacientesAsignados) {
    this.pacientesAsignados = pacientesAsignados;
  }

  public String getLesion() {
    return lesion;
  }

  public void setLesion(String lesion) {
    this.lesion = lesion;
  }

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
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

  public Set<Rutina> getRutinasCreadas() {
    return rutinasCreadas;
  }

  public void setRutinasCreadas(Set<Rutina> rutinasCreadas) {
    this.rutinasCreadas = rutinasCreadas;
  }

  public Set<Rutina> getRutinasAsignadas() {
    return rutinasAsignadas;
  }

  public void setRutinasAsignadas(Set<Rutina> rutinasAsignadas) {
    this.rutinasAsignadas = rutinasAsignadas;
  }

}
