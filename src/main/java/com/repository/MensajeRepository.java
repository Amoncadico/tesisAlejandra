package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.models.Foro;
import com.models.Mensaje;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    // Obtener todos los mensajes de un foro ordenados por fecha
    List<Mensaje> findByForoOrderByFechaAsc(Foro foro);
    
    // Obtener los últimos N mensajes de un foro
    List<Mensaje> findTop50ByForoOrderByFechaDesc(Foro foro);
}
