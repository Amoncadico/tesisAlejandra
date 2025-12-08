package com.controllers;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
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

import com.dto.CrearRutinaRequest;
import com.dto.RutinaResponseDTO;
import com.models.EDiaSemana;
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
    public ResponseEntity<List<RutinaResponseDTO>> listarRutinas() {
        List<Rutina> rutinas = rutinaRepository.findAll();
        List<RutinaResponseDTO> rutinasDTO = new java.util.ArrayList<>();
        
        for (Rutina rutina : rutinas) {
            RutinaResponseDTO dto = mapearRutinaADTO(rutina);
            rutinasDTO.add(dto);
        }
        
        return ResponseEntity.ok(rutinasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutinaResponseDTO> rutinaPorId(@PathVariable Long id) {
        Rutina rutina = rutinaRepository.findById(id).orElse(null);
        if (rutina != null) {
            RutinaResponseDTO dto = mapearRutinaADTO(rutina);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<RutinaResponseDTO>> rutinasPorPaciente(@PathVariable Long pacienteId) {
        List<Rutina> rutinas = rutinaRepository.findByPacienteId(pacienteId);
        List<RutinaResponseDTO> rutinasDTO = new java.util.ArrayList<>();
        
        for (Rutina rutina : rutinas) {
            RutinaResponseDTO dto = mapearRutinaADTO(rutina);
            rutinasDTO.add(dto);
        }
        
        return ResponseEntity.ok(rutinasDTO);
    }

    @GetMapping("/pacienteRutinaHoy")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    public ResponseEntity<?> miRutinaHoy(Authentication authentication) {
        try {
            // Obtener el paciente autenticado
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long pacienteId = userDetails.getId();
            
            // Obtener el día de la semana actual
            LocalDate hoy = LocalDate.now();
            DayOfWeek diaActual = hoy.getDayOfWeek();
            
            // Convertir DayOfWeek a EDiaSemana
            EDiaSemana diaSemana = convertirDayOfWeekAEDiaSemana(diaActual);
            
            // Buscar rutinas del paciente
            List<Rutina> rutinas = rutinaRepository.findByPacienteId(pacienteId);
            
            // Buscar la rutina que contenga el día actual
            for (Rutina rutina : rutinas) {
                if (rutina.getDiasSemana() != null && rutina.getDiasSemana().contains(diaSemana)) {
                    RutinaResponseDTO dto = mapearRutinaADTO(rutina);
                    return ResponseEntity.ok(dto);
                }
            }
            
            // Si no hay rutina para hoy
            return ResponseEntity.ok(new MessageResponse("No hay rutina asignada para el día de hoy"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener la rutina: " + e.getMessage()));
        }
    }

    @PostMapping("/crear")
    @PreAuthorize("hasAuthority('ROLE_PROFESIONAL') or hasAuthority('ROLE_ADMIN')")
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
            rutina.setFechaCreacion(LocalDate.now());
            rutina.setProfesional(profesional);
            rutina.setPaciente(paciente);
            rutina.setFechaInicio(request.getFechaInicio());
            rutina.setFechaFin(request.getFechaFin());

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
                    
                    // Si tiene duración en segundos, es un ejercicio por tiempo
                    if (itemReq.getDuracionSegundos() != null && itemReq.getDuracionSegundos() > 0) {
                        item.setDuracion(Duration.ofSeconds(itemReq.getDuracionSegundos()));
                        // Series y repeticiones quedan en 0 o por defecto
                        item.setSeries(0);
                        item.setRepeticiones(0);
                    } else {
                        // Es un ejercicio por series y repeticiones
                        item.setSeries(itemReq.getSeries() != null ? itemReq.getSeries() : 0);
                        item.setRepeticiones(itemReq.getRepeticiones() != null ? itemReq.getRepeticiones() : 0);
                        // Duración queda null
                        item.setDuracion(null);
                    }
                    
                    if (itemReq.getObservaciones() != null && !itemReq.getObservaciones().isEmpty()) {
                        item.setObservaciones(itemReq.getObservaciones());
                    }

                    itemRepository.save(item);
                }
            }

            // Recargar la rutina con los items
            Rutina rutinaCompleta = rutinaRepository.findById(rutinaSaved.getId()).orElse(rutinaSaved);
            
            // Mapear a DTO para evitar referencias circulares
            RutinaResponseDTO responseDTO = mapearRutinaADTO(rutinaCompleta);
            
            return ResponseEntity.ok(responseDTO);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<RutinaResponseDTO> actualizarRutina(@PathVariable Long id, @RequestBody Rutina rutinaActualizada) {
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
                    RutinaResponseDTO dto = mapearRutinaADTO(actualizada);
                    return ResponseEntity.ok(dto);
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

    // Método helper para mapear Rutina a DTO
    private RutinaResponseDTO mapearRutinaADTO(Rutina rutina) {
        RutinaResponseDTO dto = new RutinaResponseDTO();
        dto.setId(rutina.getId());
        dto.setNombre(rutina.getNombre());
        dto.setDescripcion(rutina.getDescripcion());
        dto.setFechaCreacion(rutina.getFechaCreacion());
        dto.setDiasSemana(rutina.getDiasSemana());
        
        // Mapear profesional y paciente de forma segura
        if (rutina.getProfesional() != null) {
            dto.setProfesionalId(rutina.getProfesional().getId());
            dto.setProfesionalUsername(rutina.getProfesional().getUsername());
        }
        
        if (rutina.getPaciente() != null) {
            dto.setPacienteId(rutina.getPaciente().getId());
            dto.setPacienteUsername(rutina.getPaciente().getUsername());
        }
        
        // Mapear items
        if (rutina.getItems() != null && !rutina.getItems().isEmpty()) {
            java.util.List<RutinaResponseDTO.ItemResponseDTO> itemsDTO = new java.util.ArrayList<>();
            for (Item item : rutina.getItems()) {
                RutinaResponseDTO.ItemResponseDTO itemDTO = new RutinaResponseDTO.ItemResponseDTO();
                itemDTO.setId(item.getId());
                if (item.getEjercicio() != null) {
                    itemDTO.setEjercicioId(item.getEjercicio().getId());
                    itemDTO.setEjercicioNombre(item.getEjercicio().getNombre());
                    itemDTO.setEjercicioDescripcion(item.getEjercicio().getDescripcion());
                    itemDTO.setEjercicioVideo(item.getEjercicio().getVideo());
                    itemDTO.setEjercicioImagen(item.getEjercicio().getImagen());
                }
                itemDTO.setSeries(item.getSeries());
                itemDTO.setRepeticiones(item.getRepeticiones());
                itemDTO.setDuracionSegundos(item.getDuracion() != null ? item.getDuracion().getSeconds() : null);
                itemDTO.setObservaciones(item.getObservaciones());
                itemsDTO.add(itemDTO);
            }
            dto.setItems(itemsDTO);
        }
        
        return dto;
    }

    // Método helper para convertir DayOfWeek a EDiaSemana
    private EDiaSemana convertirDayOfWeekAEDiaSemana(DayOfWeek dayOfWeek) {
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
