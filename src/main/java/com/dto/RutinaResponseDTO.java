package com.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.models.EDiaSemana;


public class RutinaResponseDTO {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaCreacion;
    private Set<EDiaSemana> diasSemana;
    private Long profesionalId;
    private String profesionalUsername;
    private Long pacienteId;
    private String pacienteUsername;
    private List<ItemResponseDTO> items;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public RutinaResponseDTO() {
    }

    public RutinaResponseDTO(Long id, String nombre, String descripcion, LocalDate fechaCreacion,
                            Set<EDiaSemana> diasSemana, Long profesionalId, String profesionalUsername,
                            Long pacienteId, String pacienteUsername, List<ItemResponseDTO> items,
                            LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.diasSemana = diasSemana;
        this.profesionalId = profesionalId;
        this.profesionalUsername = profesionalUsername;
        this.pacienteId = pacienteId;
        this.pacienteUsername = pacienteUsername;
        this.items = items;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Set<EDiaSemana> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Set<EDiaSemana> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public Long getProfesionalId() {
        return profesionalId;
    }

    public void setProfesionalId(Long profesionalId) {
        this.profesionalId = profesionalId;
    }

    public String getProfesionalUsername() {
        return profesionalUsername;
    }

    public void setProfesionalUsername(String profesionalUsername) {
        this.profesionalUsername = profesionalUsername;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPacienteUsername() {
        return pacienteUsername;
    }

    public void setPacienteUsername(String pacienteUsername) {
        this.pacienteUsername = pacienteUsername;
    }

    public List<ItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemResponseDTO> items) {
        this.items = items;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public static class ItemResponseDTO {
        private Long id;
        private Long ejercicioId;
        private String ejercicioNombre;
        private String ejercicioDescripcion;
        private String ejercicioVideo;
        private String ejercicioImagen;
        private Integer series;
        private Integer repeticiones;
        private Long duracionSegundos;
        private String observaciones;

        public ItemResponseDTO() {
        }

        public ItemResponseDTO(Long id, Long ejercicioId, String ejercicioNombre, 
                              String ejercicioDescripcion, String ejercicioVideo, String ejercicioImagen,
                              Integer series, Integer repeticiones, Long duracionSegundos, 
                              String observaciones) {
            this.id = id;
            this.ejercicioId = ejercicioId;
            this.ejercicioNombre = ejercicioNombre;
            this.ejercicioDescripcion = ejercicioDescripcion;
            this.ejercicioVideo = ejercicioVideo;
            this.ejercicioImagen = ejercicioImagen;
            this.series = series;
            this.repeticiones = repeticiones;
            this.duracionSegundos = duracionSegundos;
            this.observaciones = observaciones;
        }

        // Getters y Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getEjercicioId() {
            return ejercicioId;
        }

        public void setEjercicioId(Long ejercicioId) {
            this.ejercicioId = ejercicioId;
        }

        public String getEjercicioNombre() {
            return ejercicioNombre;
        }

        public void setEjercicioNombre(String ejercicioNombre) {
            this.ejercicioNombre = ejercicioNombre;
        }

        public String getEjercicioDescripcion() {
            return ejercicioDescripcion;
        }

        public void setEjercicioDescripcion(String ejercicioDescripcion) {
            this.ejercicioDescripcion = ejercicioDescripcion;
        }

        public String getEjercicioVideo() {
            return ejercicioVideo;
        }

        public void setEjercicioVideo(String ejercicioVideo) {
            this.ejercicioVideo = ejercicioVideo;
        }

        public String getEjercicioImagen() {
            return ejercicioImagen;
        }

        public void setEjercicioImagen(String ejercicioImagen) {
            this.ejercicioImagen = ejercicioImagen;
        }

        public Integer getSeries() {
            return series;
        }

        public void setSeries(Integer series) {
            this.series = series;
        }

        public Integer getRepeticiones() {
            return repeticiones;
        }

        public void setRepeticiones(Integer repeticiones) {
            this.repeticiones = repeticiones;
        }

        public Long getDuracionSegundos() {
            return duracionSegundos;
        }

        public void setDuracionSegundos(Long duracionSegundos) {
            this.duracionSegundos = duracionSegundos;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }
}
