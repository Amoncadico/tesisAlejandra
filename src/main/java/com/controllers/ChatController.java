package com.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.dto.ChatMensajeRequest;
import com.dto.MensajeDTO;
import com.models.Foro;
import com.models.Mensaje;
import com.models.User;
import com.repository.ForoRepository;
import com.repository.MensajeRepository;
import com.repository.UserRepository;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private ForoRepository foroRepository;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMensajeRequest request, Authentication authentication) {
        try {
            // Obtener el usuario autenticado
            String username = authentication.getName();
            User emisor = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener el foro
            Foro foro = foroRepository.findById(request.getForoId())
                    .orElseThrow(() -> new RuntimeException("Foro no encontrado"));

            // Verificar que el usuario sea parte del foro
            if (!foro.getProfesional().getId().equals(emisor.getId()) &&
                !foro.getPaciente().getId().equals(emisor.getId())) {
                throw new RuntimeException("Usuario no autorizado para este foro");
            }

            // Crear y guardar el mensaje
            Mensaje mensaje = new Mensaje();
            mensaje.setForo(foro);
            mensaje.setEmisor(emisor);
            mensaje.setContenido(request.getContenido());
            mensaje.setTipo(request.getTipo() != null ? request.getTipo() : "text");
            mensaje.setFecha(LocalDateTime.now());

            Mensaje mensajeGuardado = mensajeRepository.save(mensaje);

            // Crear DTO para enviar por WebSocket
            MensajeDTO mensajeDTO = new MensajeDTO();
            mensajeDTO.setId(mensajeGuardado.getId());
            mensajeDTO.setForoId(foro.getId());
            mensajeDTO.setEmisorId(emisor.getId());
            mensajeDTO.setEmisorUsername(emisor.getUsername());
            mensajeDTO.setTipo(mensajeGuardado.getTipo());
            mensajeDTO.setContenido(mensajeGuardado.getContenido());
            mensajeDTO.setFecha(mensajeGuardado.getFecha());

            // Enviar el mensaje a todos los participantes del foro
            messagingTemplate.convertAndSend("/topic/foro/" + foro.getId(), mensajeDTO);

        } catch (Exception e) {
            // Manejar errores
            messagingTemplate.convertAndSend("/topic/errors",
                new MessageError("Error al enviar mensaje: " + e.getMessage()));
        }
    }

    // Clase interna para errores
    private static class MessageError {
        private String error;

        public MessageError(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
