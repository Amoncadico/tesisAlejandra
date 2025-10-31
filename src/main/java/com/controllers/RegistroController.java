package com.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.Registro;
import com.repository.RegistroRepository;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/registro")
public class RegistroController {

    private final RegistroRepository registroRepository;

    public RegistroController(RegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Registro>> listarRegistros() {
        List<Registro> registros = registroRepository.findAll();
        return ResponseEntity.ok(registros);
    }
    
    
}
