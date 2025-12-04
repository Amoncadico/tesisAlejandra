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

    @GetMapping("/{id}")
    public ResponseEntity<Registro> registroPorId(@PathVariable Long id) {
        Registro registro = registroRepository.findById(id).orElse(null);
        return registro != null ?
                        ResponseEntity.ok(registro) : ResponseEntity.notFound().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<Registro> crearRegistro(@RequestBody Registro registro) {
        Registro nuevoRegistro = registroRepository.save(registro);
        return new ResponseEntity<>(nuevoRegistro, HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Registro> actualizarRegistro(@PathVariable Long id, @RequestBody Registro registroActualizado) {
        return registroRepository.findById(id)
                .map(registro -> {

                    if (registroActualizado.getFecha() != null) {
                        registro.setFecha(registroActualizado.getFecha());
                    }
                    
                    registro.setRealizardo(registroActualizado.getRealizardo());
                    registro.setIntencidad(registroActualizado.getIntencidad());
                    registro.setMolestias(registroActualizado.getMolestias());

                    if (registroActualizado.getObservaciones() != null && !registroActualizado.getObservaciones().isEmpty()) {
                        registro.setObservaciones(registroActualizado.getObservaciones());
                    }
                    if (registroActualizado.getRutina() != null) {
                        registro.setRutina(registroActualizado.getRutina());
                    }

                    Registro actualizado = registroRepository.save(registro);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<HttpStatus> eliminarRegistro(@PathVariable Long id) {
        Registro registro = registroRepository.findById(id).orElse(null);
        if (registro == null) {
            return ResponseEntity.notFound().build();
        }
        registroRepository.delete(registro);
        return ResponseEntity.noContent().build();
    }
    
}
