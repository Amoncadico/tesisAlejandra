package com.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.User;

//for Angular Client (withCredentials)
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/test")
public class TestController {

  @Autowired
  private ListCrudRepository<User, Long> UserRepository;

  @GetMapping("/all")
  public String allAccess() {
    return "Contenido publico.";
  }

  @GetMapping("/eva")
  @PreAuthorize("hasRole('EVALUADOR') or hasRole('VISADOR') or hasRole('ADMIN')")
  public String evaluadorAccess() {
    return "Contenido del evaluador.";
  }

  @GetMapping("/vis")
  @PreAuthorize("hasRole('VISADOR')")
  public String visadorAccess() {
    return "Contenido del visador.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Funciones de administrador";
  }

  @GetMapping("/usuarios")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> listarUsuarios() {
    List<User> usuarios = UserRepository.findAll();
    return new ResponseEntity<>(usuarios, HttpStatus.OK);
  }
}
