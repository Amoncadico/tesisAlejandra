package com.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/{id}")
    public ResponseEntity<Ejercicio> ejercicioPorId(@PathVariable Long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id).orElse(null);
        return ejercicio != null ?
                        ResponseEntity.ok(ejercicio) : ResponseEntity.notFound().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<Ejercicio> crearEjercicio(@RequestBody Ejercicio ejercicio) {
        Ejercicio nuevoEjercicio = ejercicioRepository.save(ejercicio);
        return new ResponseEntity<>(nuevoEjercicio, HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Ejercicio> actualizarEjercicio(@PathVariable Long id, @RequestBody Ejercicio ejercicioActualizado) {
        return ejercicioRepository.findById(id)
                .map(ejercicio -> {
                    // Actualizar solo si vienen valores no nulos / no vacíos
                    if (ejercicioActualizado.getNombre() != null && !ejercicioActualizado.getNombre().isEmpty()) {
                        ejercicio.setNombre(ejercicioActualizado.getNombre());
                    }
                    if (ejercicioActualizado.getZona() != null && !ejercicioActualizado.getZona().isEmpty()) {
                        ejercicio.setZona(ejercicioActualizado.getZona());
                    }
                    if (ejercicioActualizado.getCategoria() != null && !ejercicioActualizado.getCategoria().isEmpty()) {
                        ejercicio.setCategoria(ejercicioActualizado.getCategoria());
                    }
                    if (ejercicioActualizado.getDificultad() != null && !ejercicioActualizado.getDificultad().isEmpty()) {
                        ejercicio.setDificultad(ejercicioActualizado.getDificultad());
                    }
                    if (ejercicioActualizado.getDescripcion() != null && !ejercicioActualizado.getDescripcion().isEmpty()) {
                        ejercicio.setDescripcion(ejercicioActualizado.getDescripcion());
                    }
                    if (ejercicioActualizado.getUrl() != null && !ejercicioActualizado.getUrl().isEmpty()) {
                        ejercicio.setUrl(ejercicioActualizado.getUrl());
                    }

                    Ejercicio actualizado = ejercicioRepository.save(ejercicio);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<HttpStatus> eliminarEjercicio(@PathVariable Long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id).orElse(null);
        if (ejercicio == null) {
            return ResponseEntity.notFound().build();
        }
        ejercicioRepository.delete(ejercicio);
        return ResponseEntity.noContent().build();
    }

}
 