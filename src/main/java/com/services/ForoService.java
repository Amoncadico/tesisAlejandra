package com.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dto.ForoDTO;
import com.dto.MensajeDTO;
import com.models.ERole;
import com.models.Foro;
import com.models.Mensaje;
import com.models.User;
import com.repository.ForoRepository;
import com.repository.MensajeRepository;
import com.repository.UserRepository;

@Service
public class ForoService {

    @Autowired
    private ForoRepository foroRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ForoDTO crearForo(Long profesionalId, Long pacienteId) {
        // Validar que el profesional exista y tenga el rol correcto
        User profesional = userRepository.findById(profesionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        boolean esProfesional = profesional.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_PROFESIONAL) || 
                                  role.getName().equals(ERole.ROLE_ADMIN));

        if (!esProfesional) {
            throw new RuntimeException("El usuario no es un profesional");
        }

        // Validar que el paciente exista y tenga el rol correcto
        User paciente = userRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        boolean esPaciente = paciente.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_PACIENTE));

        if (!esPaciente) {
            throw new RuntimeException("El usuario no es un paciente");
        }

        // Verificar que el paciente no tenga ya un foro
        if (foroRepository.existsByPaciente(paciente)) {
            throw new RuntimeException("El paciente ya tiene un foro asignado");
        }

        // Crear el foro
        Foro foro = new Foro(profesional, paciente);
        Foro foroGuardado = foroRepository.save(foro);

        return convertirAForoDTO(foroGuardado);
    }

    public List<ForoDTO> obtenerForosDeUsuario(Long userId) {
        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Foro> foros = foroRepository.findByUsuario(usuario);

        return foros.stream()
                .map(this::convertirAForoDTO)
                .collect(Collectors.toList());
    }

    public ForoDTO obtenerForoPorId(Long foroId) {
        Foro foro = foroRepository.findById(foroId)
                .orElseThrow(() -> new RuntimeException("Foro no encontrado"));

        return convertirAForoDTO(foro);
    }

    public List<MensajeDTO> obtenerMensajesDeForo(Long foroId) {
        Foro foro = foroRepository.findById(foroId)
                .orElseThrow(() -> new RuntimeException("Foro no encontrado"));

        List<Mensaje> mensajes = mensajeRepository.findByForoOrderByFechaAsc(foro);

        return mensajes.stream()
                .map(this::convertirAMensajeDTO)
                .collect(Collectors.toList());
    }

    public boolean usuarioPerteneceAForo(Long userId, Long foroId) {
        Foro foro = foroRepository.findById(foroId)
                .orElseThrow(() -> new RuntimeException("Foro no encontrado"));

        return foro.getProfesional().getId().equals(userId) || 
               foro.getPaciente().getId().equals(userId);
    }

    private ForoDTO convertirAForoDTO(Foro foro) {
        ForoDTO dto = new ForoDTO();
        dto.setId(foro.getId());
        dto.setProfesionalId(foro.getProfesional().getId());
        dto.setProfesionalUsername(foro.getProfesional().getUsername());
        dto.setPacienteId(foro.getPaciente().getId());
        dto.setPacienteUsername(foro.getPaciente().getUsername());

        // Obtener el último mensaje si existe
        List<Mensaje> ultimosMensajes = mensajeRepository.findTop50ByForoOrderByFechaDesc(foro);
        if (!ultimosMensajes.isEmpty()) {
            dto.setUltimoMensaje(convertirAMensajeDTO(ultimosMensajes.get(0)));
        }

        return dto;
    }

    private MensajeDTO convertirAMensajeDTO(Mensaje mensaje) {
        MensajeDTO dto = new MensajeDTO();
        dto.setId(mensaje.getId());
        dto.setForoId(mensaje.getForo().getId());
        dto.setEmisorId(mensaje.getEmisor().getId());
        dto.setEmisorUsername(mensaje.getEmisor().getUsername());
        dto.setTipo(mensaje.getTipo());
        dto.setContenido(mensaje.getContenido());
        dto.setTitulo(mensaje.getTitulo());
        dto.setDescripcion(mensaje.getDescripcion());
        dto.setIconoUrl(mensaje.getIconoUrl());
        dto.setImagenFondo(mensaje.getImagenFondo());
        dto.setFecha(mensaje.getFecha());
        return dto;
    }
}
