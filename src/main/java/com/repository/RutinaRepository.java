package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.models.Rutina;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    List<Rutina> findByPacienteId(Long pacienteId);
}
