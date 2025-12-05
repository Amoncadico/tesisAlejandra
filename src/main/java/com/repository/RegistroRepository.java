package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.models.Registro;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
    List<Registro> findByRutinaId(Long rutinaId);
    List<Registro> findByRutinaPacienteId(Long pacienteId);
    List<Registro> findByRutinaProfesionalId(Long profesionalId);
}
