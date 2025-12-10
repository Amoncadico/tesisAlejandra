package com.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.UserProfileDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.User;
import com.repository.MensajeRepository;
import com.repository.UserRepository;
import com.security.services.UserDetailsImpl;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UserRepository userRepository;
    private final com.repository.ForoRepository foroRepository;
    private final MensajeRepository mensajeRepository;

    public UsuarioController(UserRepository userRepository, com.repository.ForoRepository foroRepository, MensajeRepository mensajeRepository) {
        this.userRepository = userRepository;
        this.foroRepository = foroRepository;
        this.mensajeRepository = mensajeRepository;
    }

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> listarUsuarios() {
        List<User> usuarios = userRepository.findAll();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/pacientePerfil")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE') or hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserProfileDTO> getPacienteAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<User> usuarioOptional = userRepository.findById(userDetails.getId());

        if (usuarioOptional.isPresent()) {
            User usuario = usuarioOptional.get();

            String profesionalUsername = null;
            if (usuario.getProfesionalAsignado() != null) {
                profesionalUsername = usuario.getProfesionalAsignado().getUsername();
            }

            UserProfileDTO dto = new UserProfileDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRut(),
                usuario.getFechaNacimiento(),
                usuario.getLesion(),
                usuario.getFechaRegistro(),
                usuario.getFoto(),
                profesionalUsername,
                null // cantidadPacientes no aplica para paciente
            );
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profesionalPerfil")
    @PreAuthorize("hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserProfileDTO> getProfesionalAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<User> usuarioOptional = userRepository.findById(userDetails.getId());

        if (usuarioOptional.isPresent()) {
            User usuario = usuarioOptional.get();
            int cantidadPacientes = 0;
            if (usuario.getPacientesAsignados() != null) {
                cantidadPacientes = usuario.getPacientesAsignados().size();
            }
            // Se asume que UserProfileDTO tiene un campo extra para cantidadPacientes
            UserProfileDTO dto = new UserProfileDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRut(),
                usuario.getFechaNacimiento(),
                usuario.getLesion(),
                usuario.getFechaRegistro(),
                usuario.getFoto(),
                null, // Profesional no tiene profesional asignado
                cantidadPacientes
            );
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/mis-pacientes")
    @PreAuthorize("hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserProfileDTO>> getMisPacientes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> profesionalOptional = userRepository.findById(userDetails.getId());

        if (profesionalOptional.isPresent()) {
            User profesional = profesionalOptional.get();
            Set<User> pacientes = profesional.getPacientesAsignados();
            
            List<UserProfileDTO> pacientesDTO = new ArrayList<>();
            
            for (User paciente : pacientes) {
                UserProfileDTO dto = new UserProfileDTO(
                    paciente.getId(),
                    paciente.getUsername(),
                    paciente.getRut(),
                    paciente.getFechaNacimiento(),
                    paciente.getLesion(),
                    paciente.getFechaRegistro(),
                    paciente.getFoto(),
                    profesional.getUsername(), // El profesional autenticado
                    null // cantidadPacientes no aplica para paciente
                );
                pacientesDTO.add(dto);
            }
            
            return new ResponseEntity<>(pacientesDTO, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profesionales")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_PROFESIONAL')")
    public ResponseEntity<List<UserProfileDTO>> listarProfesionales() {
        java.util.List<User> profesionales = userRepository.findByRoles_NameIn(
            java.util.Arrays.asList(com.models.ERole.ROLE_PROFESIONAL, com.models.ERole.ROLE_ADMIN)
        );

        java.util.List<UserProfileDTO> resultado = profesionales.stream().map(usuario -> {
            int cantidadPacientes = usuario.getPacientesAsignados() != null ? usuario.getPacientesAsignados().size() : 0;
            return new UserProfileDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRut(),
                usuario.getFechaNacimiento(),
                usuario.getLesion(),
                usuario.getFechaRegistro(),
                usuario.getFoto(),
                null, // profesionalAsignado no aplica para profesional
                cantidadPacientes
            );
        }).toList();

        return ResponseEntity.ok(resultado);
    }

    

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUsuario(@PathVariable Long userId) {
        Optional<User> usuarioOptional = userRepository.findById(userId);

        if (usuarioOptional.isPresent()) {
            User usuario = usuarioOptional.get();
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("eliminar/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long userId) {
        // Buscar el usuario por ID
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Realizar la lógica para eliminar el usuario (en este caso, simplemente eliminarlo de la base de datos)
        userRepository.delete(user);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('PROFESIONAL') or hasRole('ADMIN')")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long userId, @RequestBody JsonNode payload) {
        Optional<User> usuarioOptional = userRepository.findById(userId);

        if (usuarioOptional.isPresent()) {
            User usuario = usuarioOptional.get();
            // Convertir payload a User para los campos normales (profesionalAsignado estará ignorado por @JsonIgnore)
            User usuarioActualizado = null;
            try {
                usuarioActualizado = objectMapper.treeToValue(payload, User.class);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new com.payload.response.MessageResponse("Payload inválido"));
            }

            // Actualizar los campos relevantes del usuario con la información proporcionada en usuarioActualizado
            if(usuarioActualizado.getUsername() != null && !usuarioActualizado.getUsername().isEmpty()) {
                usuario.setUsername(usuarioActualizado.getUsername());
            }
            if(usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
                usuario.setPassword(encoder.encode(usuarioActualizado.getPassword()));
            }
            if(usuarioActualizado.getRut() != null) {
                usuario.setRut(usuarioActualizado.getRut());
            }
            if(usuarioActualizado.getLesion() != null) {
                usuario.setLesion(usuarioActualizado.getLesion());
            }
            if(usuarioActualizado.getFechaNacimiento() != null) {
                usuario.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
            }
            if(usuarioActualizado.getFoto() != null) {
                usuario.setFoto(usuarioActualizado.getFoto());
            }
            
            // Si se cambia el profesional asignado, validar y actualizar también el foro y las relaciones inversas
            if (payload.has("profesionalAsignado") && payload.get("profesionalAsignado").has("id") && !payload.get("profesionalAsignado").get("id").isNull()) {
                Long nuevoProfesionalId = payload.get("profesionalAsignado").get("id").asLong();
                // Buscar el nuevo profesional
                User nuevoProfesional = userRepository.findById(nuevoProfesionalId).orElse(null);
                if (nuevoProfesional == null) {
                    // Devolvemos 400 para que el cliente sepa que el id no es válido
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new com.payload.response.MessageResponse("Profesional con id " + nuevoProfesionalId + " no encontrado"));
                }

                // Actualizar la relación en el usuario (lado propietario)
                User profesionalAnterior = usuario.getProfesionalAsignado();
                usuario.setProfesionalAsignado(nuevoProfesional);

                // Actualizar colecciones inversas: quitar paciente del profesionalAnterior y agregar al nuevoProfesional
                if (profesionalAnterior != null && profesionalAnterior.getPacientesAsignados() != null) {
                    profesionalAnterior.getPacientesAsignados().removeIf(p -> p.getId().equals(usuario.getId()));
                    userRepository.save(profesionalAnterior);
                }

                if (nuevoProfesional.getPacientesAsignados() == null) {
                    nuevoProfesional.setPacientesAsignados(new java.util.HashSet<>());
                }
                // Asegurar que el set contiene al paciente
                boolean ya = nuevoProfesional.getPacientesAsignados().stream().anyMatch(p -> p.getId().equals(usuario.getId()));
                if (!ya) {
                    nuevoProfesional.getPacientesAsignados().add(usuario);
                    userRepository.save(nuevoProfesional);
                }

                // Actualizar el foro del paciente, si existe: borrar mensajes previos y reasignar profesional
                foroRepository.findByPacienteId(usuario.getId()).ifPresent(foro -> {
                    try {
                        mensajeRepository.deleteByForo(foro);
                    } catch (Exception ex) {
                        System.out.println("[WARN] Error borrando mensajes del foro " + foro.getId() + ": " + ex.getMessage());
                    }

                    foro.setProfesional(nuevoProfesional);
                    foroRepository.save(foro);
                });
            }
            
            // Guardar el usuario actualizado
            userRepository.save(usuario);

            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/editar-paciente/{pacienteId}")
    @PreAuthorize("hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> editarPacienteAsociado(@PathVariable Long pacienteId, @RequestBody User usuarioActualizado, Authentication authentication) {
        // Obtener profesional autenticado
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long profesionalId = userDetails.getId();

        User profesional = userRepository.findById(profesionalId).orElse(null);
        if (profesional == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new com.payload.response.MessageResponse("Profesional no encontrado"));
        }

        User paciente = userRepository.findById(pacienteId).orElse(null);
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }

        // Permisos: si no es ADMIN, verificar que el paciente esté asignado al profesional
        boolean isAdmin = profesional.getRoles().stream()
                .anyMatch(r -> r.getName().equals(com.models.ERole.ROLE_ADMIN));

        if (!isAdmin) {
            if (paciente.getProfesionalAsignado() == null || !paciente.getProfesionalAsignado().getId().equals(profesionalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new com.payload.response.MessageResponse("No tienes permiso para editar este paciente"));
            }
        }

        // Actualizar campos permitidos
        if (usuarioActualizado.getUsername() != null && !usuarioActualizado.getUsername().isEmpty()) {
            paciente.setUsername(usuarioActualizado.getUsername());
        }
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            paciente.setPassword(encoder.encode(usuarioActualizado.getPassword()));
        }
        if (usuarioActualizado.getRut() != null) {
            paciente.setRut(usuarioActualizado.getRut());
        }
        if (usuarioActualizado.getLesion() != null) {
            paciente.setLesion(usuarioActualizado.getLesion());
        }
        if (usuarioActualizado.getFechaNacimiento() != null) {
            paciente.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
        }
        if (usuarioActualizado.getFoto() != null) {
            paciente.setFoto(usuarioActualizado.getFoto());
        }

        // No permitimos cambiar roles ni profesionalAsignado aquí (salvo vía otro endpoint)

        User actualizado = userRepository.save(paciente);

        // Devolver UserProfileDTO como en otros endpoints
        String profesionalUsername = null;
        if (actualizado.getProfesionalAsignado() != null) {
            profesionalUsername = actualizado.getProfesionalAsignado().getUsername();
        }

        UserProfileDTO dto = new UserProfileDTO(
                actualizado.getId(),
                actualizado.getUsername(),
                actualizado.getRut(),
                actualizado.getFechaNacimiento(),
                actualizado.getLesion(),
                actualizado.getFechaRegistro(),
                actualizado.getFoto(),
                profesionalUsername,
                null
        );

        return ResponseEntity.ok(dto);
    }

}
