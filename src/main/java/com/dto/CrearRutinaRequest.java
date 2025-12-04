package com.dto;

import java.util.List;
import java.util.Set;

import com.models.EDiaSemana;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearRutinaRequest {
    
    @NotBlank
    private String nombre;
    
    private String descripcion;
    
    @NotNull
    private Long pacienteId;
    
    private Set<EDiaSemana> diasSemana;
    
    private List<ItemRequest> items;

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

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Set<EDiaSemana> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Set<EDiaSemana> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public List<ItemRequest> getItems() {
        return items;
    }

    public void setItems(List<ItemRequest> items) {
        this.items = items;
    }

    public static class ItemRequest {
        @NotNull
        private Long ejercicioId;
        
        private int series;
        private int repeticiones;
        private Long duracionSegundos; // Duración en segundos
        private String observaciones;

        public Long getEjercicioId() {
            return ejercicioId;
        }

        public void setEjercicioId(Long ejercicioId) {
            this.ejercicioId = ejercicioId;
        }

        public int getSeries() {
            return series;
        }

        public void setSeries(int series) {
            this.series = series;
        }

        public int getRepeticiones() {
            return repeticiones;
        }

        public void setRepeticiones(int repeticiones) {
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
