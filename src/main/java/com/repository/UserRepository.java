package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);
  
  // Encontrar usuarios por el nombre del rol (ej. ROLE_PROFESIONAL)
  java.util.List<User> findByRoles_Name(com.models.ERole name);
  
  // Encontrar usuarios que tengan cualquiera de los roles indicados
  java.util.List<User> findByRoles_NameIn(java.util.Collection<com.models.ERole> names);
}
