package com.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.LogroMensajeDTO;
import com.models.EDiaSemana;
import com.models.Foro;
import com.models.Rutina;
import com.repository.RegistroRepository;
import com.repository.RutinaRepository;
import com.security.services.UserDetailsImpl;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/logros")
public class LogrosController {
    
    private final RutinaRepository rutinaRepository;
    private final RegistroRepository registroRepository;
    
    @org.springframework.beans.factory.annotation.Autowired
    private com.repository.ForoRepository foroRepository;

    public LogrosController(RutinaRepository rutinaRepository, RegistroRepository registroRepository) {
        this.rutinaRepository = rutinaRepository;
        this.registroRepository = registroRepository;
    }

    @GetMapping("/logros-paciente")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    public ResponseEntity<?> obtenerLogrosPaciente(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long pacienteId = userDetails.getId();

        // Buscar el foro asociado al paciente
        Foro foro = foroRepository.findByPacienteId(pacienteId).orElse(null);
        if (foro == null) {
            return ResponseEntity.badRequest().body("Foro no encontrado para el paciente");
        }

        // Buscar mensajes de tipo "logro" asociados al foro y filtrados por el emisor
        List<LogroMensajeDTO> mensajesLogro = foro.getMensajes().stream()
            .filter(mensaje -> "logro".equalsIgnoreCase(mensaje.getTipo()) && mensaje.getEmisor().getId().equals(pacienteId))
            .map(mensaje -> new LogroMensajeDTO(
                mensaje.getId(),
                mensaje.getTipo(),
                mensaje.getContenido(),
                mensaje.getTitulo(),
                mensaje.getDescripcion(),
                mensaje.getIconoUrl(),
                mensaje.getImagenFondo(),
                mensaje.getFecha()
            ))
            .toList();

        return ResponseEntity.ok(mensajesLogro);
    }

    @PostMapping("/notificar-logro")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    public ResponseEntity<?> notificarLogro(@RequestBody java.util.Map<String, Integer> body, Authentication authentication) {
        Integer tipo = body.get("tipo");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long pacienteId = userDetails.getId();

        // Buscar el foro asociado al paciente
        Foro foro = foroRepository.findByPacienteId(pacienteId).orElse(null);
        if (foro == null) {
            return ResponseEntity.badRequest().body("Foro no encontrado para el paciente");
        }
        System.out.println("Foro encontrado: " + foro.getId());

        // Actualizar el logro correspondiente en el foro
        switch (tipo) {
            case 1:
                foro.setLogroImpulso(true);
                break;
            case 2:
                foro.setLogroRitmo(true);
                break;
            case 3:
                foro.setLogroDominio(true);
                break;
            default:
                return ResponseEntity.badRequest().body("Tipo de logro no válido");
        }

        // Guardar los cambios en el foro
        foroRepository.save(foro);
        return ResponseEntity.ok("Logro actualizado correctamente en el foro");
    }

    @GetMapping("/dias-ejercicio")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    public ResponseEntity<?> contarDiasEjercicioPaciente(Authentication authentication){

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long pacienteId = userDetails.getId();

        int totalDias = calcularDiasEjercicioPorPaciente(pacienteId);
        int totalLlevado = contarRegistrosPorPaciente(pacienteId);
        java.util.Map<String, Integer> resultado = new java.util.HashMap<>();
        
        resultado.put("totalDias", totalDias);
        resultado.put("totalLlevado", totalLlevado);
        return ResponseEntity.ok(resultado);
    }

    // Función auxiliar para contar registros asociados a todas las rutinas de un paciente
    public int contarRegistrosPorPaciente(Long pacienteId) {
        return registroRepository.findByRutinaPacienteId(pacienteId).size();
    }

    // Función auxiliar para calcular días de ejercicio por paciente
    public int calcularDiasEjercicioPorPaciente(Long pacienteId) {
        List<Rutina> rutinas = rutinaRepository.findByPacienteId(pacienteId);
        int totalDias = 0;
        for (Rutina rutina : rutinas) {
            // Si diasSemana es null, ignorar la rutina
            Set<EDiaSemana> diasSemana = rutina.getDiasSemana();
            if (rutina.getFechaInicio() != null && rutina.getFechaFin() != null && diasSemana != null && !diasSemana.isEmpty()) {
                LocalDate inicio = rutina.getFechaInicio();
                LocalDate fin = rutina.getFechaFin();
                for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {
                    EDiaSemana diaSemana = convertirDayOfWeekAEDiaSemana(fecha.getDayOfWeek());
                    if (diasSemana.contains(diaSemana)) {
                        totalDias++;
                    }
                }
            }
        }
        return totalDias;
    }
    
    private EDiaSemana convertirDayOfWeekAEDiaSemana(java.time.DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return EDiaSemana.LUNES;
            case TUESDAY:
                return EDiaSemana.MARTES;
            case WEDNESDAY:
                return EDiaSemana.MIERCOLES;
            case THURSDAY:
                return EDiaSemana.JUEVES;
            case FRIDAY:
                return EDiaSemana.VIERNES;
            case SATURDAY:
                return EDiaSemana.SABADO;
            case SUNDAY:
                return EDiaSemana.DOMINGO;
            default:
                throw new IllegalArgumentException("Día de la semana no válido: " + dayOfWeek);
        }
    }

}
