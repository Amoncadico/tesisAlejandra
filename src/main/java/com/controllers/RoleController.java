package com.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.ERole;
import com.models.Role;
import com.models.User;
import com.repository.RoleRepository;
import com.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RoleController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/user-roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<String>> getUserRoles(@PathVariable Long userId) {
        // Buscar el usuario por ID
        
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Obtener los roles del usuario
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(roles);
    }

    @PostMapping("/guardar-roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> guardarRoles(@PathVariable Long userId, @RequestBody Set<String> roles) {
        // Obtener el usuario por ID
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Agregar nuevos roles al usuario
        for (String rol : roles) {
            if (!user.getRoles().stream().anyMatch(existingRole -> existingRole.getName().name().equals(rol))) {
                // Crear el nuevo objeto Role y agregarlo al conjunto de roles del usuario
                Role newRole = roleRepository.findByName(ERole.valueOf(rol))
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                user.getRoles().add(newRole);
            }
        }

        // Eliminar roles no seleccionados del usuario
        Set<String> rolesUsuario = user.getRoles().stream()
                .map(existingUserRole -> existingUserRole.getName().name())
                .collect(Collectors.toSet());

        for (String existingRole : rolesUsuario) {
            if (!roles.contains(existingRole)) {
                // Eliminar el rol del conjunto de roles del usuario
                Role roleToRemove = user.getRoles().stream()
                        .filter(existingUserRole -> existingUserRole.getName().name().equals(existingRole))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Error: Role to remove not found."));
                user.getRoles().remove(roleToRemove);
            }
        }

        // Guardar el usuario actualizado en la base de datos
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

}
