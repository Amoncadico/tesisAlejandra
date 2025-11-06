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

import com.models.Rutina;
import com.repository.RutinaRepository;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/rutina")
public class RutinaController {
    
    private final RutinaRepository rutinaRepository;

    public RutinaController(RutinaRepository rutinaRepository) {
        this.rutinaRepository = rutinaRepository;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Rutina>> listarRutinas() {
        List<Rutina> rutinas = rutinaRepository.findAll();
        return ResponseEntity.ok(rutinas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rutina> rutinaPorId(@PathVariable Long id) {
        Rutina rutina = rutinaRepository.findById(id).orElse(null);
        return rutina != null ?
                        ResponseEntity.ok(rutina) : ResponseEntity.notFound().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<Rutina> crearRutina(@RequestBody Rutina rutina) {
        Rutina nuevaRutina = rutinaRepository.save(rutina);
        return new ResponseEntity<>(nuevaRutina, HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Rutina> actualizarRutina(@PathVariable Long id, @RequestBody Rutina rutinaActualizada) {
        return rutinaRepository.findById(id)
                .map(rutina -> {

                    if (rutinaActualizada.getNombre() != null && !rutinaActualizada.getNombre().isEmpty()) {
                        rutina.setNombre(rutinaActualizada.getNombre());
                    }
                    if (rutinaActualizada.getFechaCreacion() != null) {
                        rutina.setFechaCreacion(rutinaActualizada.getFechaCreacion());
                    }
                    if (rutinaActualizada.getRegistros() != null) {
                        rutina.setRegistros(rutinaActualizada.getRegistros());
                    }
                    if (rutinaActualizada.getItems() != null) {
                        rutina.setItems(rutinaActualizada.getItems());
                    }
                    if (rutinaActualizada.getProfesional() != null) {
                        rutina.setProfesional(rutinaActualizada.getProfesional());
                    }
                    if (rutinaActualizada.getPaciente() != null) {
                        rutina.setPaciente(rutinaActualizada.getPaciente());
                    }

                    Rutina actualizada = rutinaRepository.save(rutina);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<HttpStatus> eliminarRutina(@PathVariable Long id) {
        Rutina rutina = rutinaRepository.findById(id).orElse(null);
        if (rutina == null) {
            return ResponseEntity.notFound().build();
        }
        rutinaRepository.delete(rutina);
        return ResponseEntity.noContent().build();
    }

}
