package com.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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

    
}
