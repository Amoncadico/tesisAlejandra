package com.controllers;

import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.CrearRutinaRequest;
import com.models.Ejercicio;
import com.models.Item;
import com.models.Rutina;
import com.models.User;
import com.payload.response.MessageResponse;
import com.repository.EjercicioRepository;
import com.repository.ItemRepository;
import com.repository.RutinaRepository;
import com.repository.UserRepository;
import com.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/rutina")
public class RutinaController {
    
    private final RutinaRepository rutinaRepository;
    private final UserRepository userRepository;
    private final EjercicioRepository ejercicioRepository;
    private final ItemRepository itemRepository;

    public RutinaController(RutinaRepository rutinaRepository, UserRepository userRepository, 
                           EjercicioRepository ejercicioRepository, ItemRepository itemRepository) {
        this.rutinaRepository = rutinaRepository;
        this.userRepository = userRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.itemRepository = itemRepository;
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
    public ResponseEntity<?> crearRutinaCompleta(@Valid @RequestBody CrearRutinaRequest request, Authentication authentication) {
        try {
            // Obtener el usuario autenticado (profesional)
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User profesional = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

            // Validar que el paciente existe
            User paciente = userRepository.findById(request.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

            // Crear la rutina
            Rutina rutina = new Rutina();
            rutina.setNombre(request.getNombre());
            rutina.setDescripcion(request.getDescripcion());
            rutina.setFechaCreacion(new Date());
            rutina.setProfesional(profesional);
            rutina.setPaciente(paciente);
            
            if (request.getDiasSemana() != null && !request.getDiasSemana().isEmpty()) {
                rutina.setDiasSemana(request.getDiasSemana());
            } else {
                rutina.setDiasSemana(new HashSet<>());
            }

            // Guardar la rutina primero
            Rutina rutinaSaved = rutinaRepository.save(rutina);

            // Crear y asociar los items si existen
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                for (CrearRutinaRequest.ItemRequest itemReq : request.getItems()) {
                    Ejercicio ejercicio = ejercicioRepository.findById(itemReq.getEjercicioId())
                            .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado: " + itemReq.getEjercicioId()));

                    Item item = new Item();
                    item.setEjercicio(ejercicio);
                    item.setRutina(rutinaSaved);
                    item.setSeries(itemReq.getSeries());
                    item.setRepeticiones(itemReq.getRepeticiones());
                    
                    // Convertir segundos a Duration si existe
                    if (itemReq.getDuracionSegundos() != null && itemReq.getDuracionSegundos() > 0) {
                        item.setDuracion(Duration.ofSeconds(itemReq.getDuracionSegundos()));
                    }
                    
                    if (itemReq.getObservaciones() != null && !itemReq.getObservaciones().isEmpty()) {
                        item.setObservaciones(itemReq.getObservaciones());
                    }

                    itemRepository.save(item);
                }
            }

            // Recargar la rutina con los items
            Rutina rutinaCompleta = rutinaRepository.findById(rutinaSaved.getId()).orElse(rutinaSaved);
            
            return ResponseEntity.ok(rutinaCompleta);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Rutina> actualizarRutina(@PathVariable Long id, @RequestBody Rutina rutinaActualizada) {
        return rutinaRepository.findById(id)
                .map(rutina -> {

                    if (rutinaActualizada.getNombre() != null && !rutinaActualizada.getNombre().isEmpty()) {
                        rutina.setNombre(rutinaActualizada.getNombre());
                    }
                    if (rutinaActualizada.getDescripcion() != null && !rutinaActualizada.getDescripcion().isEmpty()) {
                        rutina.setDescripcion(rutinaActualizada.getDescripcion());
                    }
                    if (rutinaActualizada.getFechaCreacion() != null) {
                        rutina.setFechaCreacion(rutinaActualizada.getFechaCreacion());
                    }
                    if (rutinaActualizada.getDiasSemana() != null) {
                        rutina.setDiasSemana(rutinaActualizada.getDiasSemana());
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
