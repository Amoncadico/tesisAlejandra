package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.models.Registro;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {

}
