package com.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.ForoDTO;
import com.dto.MensajeDTO;
import com.payload.response.MessageResponse;
import com.security.services.UserDetailsImpl;
import com.services.ForoService;

@RestController
@RequestMapping("/api/foros")
public class ForoController {

    @Autowired
    private ForoService foroService;
    
    @PostMapping("/crear")
    @PreAuthorize("hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> crearForo(@RequestBody CrearForoRequest request, Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            ForoDTO foro = foroService.crearForo(userDetails.getId(), request.getPacienteId());
            
            return ResponseEntity.ok(foro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/mis-foros")
    public ResponseEntity<?> obtenerMisForos(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            List<ForoDTO> foros = foroService.obtenerForosDeUsuario(userDetails.getId());
            
            return ResponseEntity.ok(foros);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/{foroId}")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE') or hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> obtenerForo(@PathVariable Long foroId, Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Verificar que el usuario pertenezca al foro
            if (!foroService.usuarioPerteneceAForo(userDetails.getId(), foroId)) {
                return ResponseEntity.status(403)
                        .body(new MessageResponse("Error: No tienes acceso a este foro"));
            }
            
            ForoDTO foro = foroService.obtenerForoPorId(foroId);
            
            return ResponseEntity.ok(foro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/{foroId}/mensajes")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE') or hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> obtenerMensajes(@PathVariable Long foroId, Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Verificar que el usuario pertenezca al foro
            if (!foroService.usuarioPerteneceAForo(userDetails.getId(), foroId)) {
                return ResponseEntity.status(403)
                        .body(new MessageResponse("Error: No tienes acceso a este foro"));
            }
            
            List<MensajeDTO> mensajes = foroService.obtenerMensajesDeForo(foroId);
            
            return ResponseEntity.ok(mensajes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Clase interna para el request de crear foro
    public static class CrearForoRequest {
        private Long pacienteId;

        public Long getPacienteId() {
            return pacienteId;
        }

        public void setPacienteId(Long pacienteId) {
            this.pacienteId = pacienteId;
        }
    }
}
