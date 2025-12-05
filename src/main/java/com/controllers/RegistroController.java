package com.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.dto.CrearRegistroRequest;
import com.dto.RegistroResponseDTO;
import com.models.Registro;
import com.models.Rutina;
import com.payload.response.MessageResponse;
import com.repository.RegistroRepository;
import com.repository.RutinaRepository;
import com.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/registro")
public class RegistroController {

    private final RegistroRepository registroRepository;
    private final RutinaRepository rutinaRepository;

    public RegistroController(RegistroRepository registroRepository, RutinaRepository rutinaRepository) {
        this.registroRepository = registroRepository;
        this.rutinaRepository = rutinaRepository;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<RegistroResponseDTO>> listarRegistros() {
        List<Registro> registros = registroRepository.findAll();
        List<RegistroResponseDTO> registrosDTO = new java.util.ArrayList<>();
        
        for (Registro registro : registros) {
            RegistroResponseDTO dto = mapearRegistroADTO(registro);
            registrosDTO.add(dto);
        }
        
        return ResponseEntity.ok(registrosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroResponseDTO> registroPorId(@PathVariable Long id) {
        Registro registro = registroRepository.findById(id).orElse(null);
        if (registro != null) {
            RegistroResponseDTO dto = mapearRegistroADTO(registro);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/rutina/{rutinaId}")
    public ResponseEntity<List<RegistroResponseDTO>> registrosPorRutina(@PathVariable Long rutinaId) {
        List<Registro> registros = registroRepository.findByRutinaId(rutinaId);
        List<RegistroResponseDTO> registrosDTO = new java.util.ArrayList<>();
        
        for (Registro registro : registros) {
            RegistroResponseDTO dto = mapearRegistroADTO(registro);
            registrosDTO.add(dto);
        }
        
        return ResponseEntity.ok(registrosDTO);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<RegistroResponseDTO>> registrosPorPaciente(@PathVariable Long pacienteId) {
        List<Registro> registros = registroRepository.findByRutinaPacienteId(pacienteId);
        List<RegistroResponseDTO> registrosDTO = new java.util.ArrayList<>();
        
        for (Registro registro : registros) {
            RegistroResponseDTO dto = mapearRegistroADTO(registro);
            registrosDTO.add(dto);
        }
        
        return ResponseEntity.ok(registrosDTO);
    }

    @GetMapping("/mis-registros")
    @PreAuthorize("hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<RegistroResponseDTO>> misRegistros(Authentication authentication) {
        // Obtener el profesional autenticado
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long profesionalId = userDetails.getId();
        
        List<Registro> registros = registroRepository.findByRutinaProfesionalId(profesionalId);
        List<RegistroResponseDTO> registrosDTO = new java.util.ArrayList<>();
        
        for (Registro registro : registros) {
            RegistroResponseDTO dto = mapearRegistroADTO(registro);
            registrosDTO.add(dto);
        }
        
        return ResponseEntity.ok(registrosDTO);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearRegistro(@Valid @RequestBody CrearRegistroRequest request) {
        try {
            // Validar que la rutina existe
            Rutina rutina = rutinaRepository.findById(request.getRutinaId())
                    .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

            // Crear el registro
            Registro registro = new Registro();
            registro.setFecha(request.getFecha());
            registro.setObservaciones(request.getObservaciones());
            registro.setRealizardo(request.getRealizardo());
            registro.setIntencidad(request.getIntencidad());
            registro.setMolestias(request.getMolestias());
            registro.setRutina(rutina);

            Registro nuevoRegistro = registroRepository.save(registro);
            RegistroResponseDTO responseDTO = mapearRegistroADTO(nuevoRegistro);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<RegistroResponseDTO> actualizarRegistro(@PathVariable Long id, @RequestBody Registro registroActualizado) {
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
                    RegistroResponseDTO dto = mapearRegistroADTO(actualizado);
                    return ResponseEntity.ok(dto);
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

    // Método helper para mapear Registro a DTO
    private RegistroResponseDTO mapearRegistroADTO(Registro registro) {
        RegistroResponseDTO dto = new RegistroResponseDTO();
        dto.setId(registro.getId());
        dto.setFecha(registro.getFecha());
        dto.setObservaciones(registro.getObservaciones());
        dto.setRealizardo(registro.getRealizardo());
        dto.setIntencidad(registro.getIntencidad());
        dto.setMolestias(registro.getMolestias());
        
        if (registro.getRutina() != null) {
            dto.setRutinaId(registro.getRutina().getId());
            dto.setRutinaNombre(registro.getRutina().getNombre());
            
            // Agregar información del paciente
            if (registro.getRutina().getPaciente() != null) {
                dto.setPacienteNombre(registro.getRutina().getPaciente().getUsername());
                dto.setPacienteFoto(registro.getRutina().getPaciente().getFoto());
            }
        }
        
        return dto;
    }
    
}
