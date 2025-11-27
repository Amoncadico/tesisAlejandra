package com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.models.Foro;
import com.models.User;

@Repository
public interface ForoRepository extends JpaRepository<Foro, Long> {
    
    // Encontrar foro entre un profesional y un paciente
    Optional<Foro> findByProfesionalAndPaciente(User profesional, User paciente);
    
    // Encontrar todos los foros de un profesional
    List<Foro> findByProfesional(User profesional);
    
    // Encontrar el foro de un paciente (solo puede tener uno)
    Optional<Foro> findByPaciente(User paciente);
    
    // Verificar si un paciente ya tiene un foro
    boolean existsByPaciente(User paciente);
    
    // Obtener todos los foros donde participa un usuario (como profesional o paciente)
    @Query("SELECT f FROM Foro f WHERE f.profesional = ?1 OR f.paciente = ?1")
    List<Foro> findByUsuario(User usuario);
}
