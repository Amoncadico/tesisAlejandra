package com.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.Ejercicio;
import com.repository.EjercicioRepository;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/ejercicio")
public class EjercicioController {

    private final EjercicioRepository ejercicioRepository;

    public EjercicioController(EjercicioRepository ejercicioRepository) {
        this.ejercicioRepository = ejercicioRepository;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Ejercicio>> listarEjercicios() {
        List<Ejercicio> ejercicios = ejercicioRepository.findAll();
        return ResponseEntity.ok(ejercicios);
    }
    
}
 