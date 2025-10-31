package com.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.User;
import com.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/usuario") 
public class UsuarioController {

    private final UserRepository userRepository;

    public UsuarioController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> listarUsuarios() {
        List<User> usuarios = userRepository.findAll();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('EVALUADOR') or hasRole('VISADOR') or hasRole('ADMIN')")
    public ResponseEntity<User> getUsuario(@PathVariable Long userId) {
        Optional<User> usuarioOptional = userRepository.findById(userId);

        if (usuarioOptional.isPresent()) {
            User usuario = usuarioOptional.get();
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("eliminar/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long userId) {
        // Buscar el usuario por ID
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Realizar la lógica para eliminar el usuario (en este caso, simplemente eliminarlo de la base de datos)
        userRepository.delete(user);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('EVALUADOR') or hasRole('VISADOR') or hasRole('ADMIN')")
    public ResponseEntity<User> actualizarUsuario(@PathVariable Long userId, @RequestBody User usuarioActualizado) {
        Optional<User> usuarioOptional = userRepository.findById(userId);

        if (usuarioOptional.isPresent()) {
            User usuario = usuarioOptional.get();

            // Actualizar los campos relevantes del usuario con la información proporcionada en usuarioActualizado
            if(usuarioActualizado.getUsername() != "")usuario.setUsername(usuarioActualizado.getUsername());
            if(usuarioActualizado.getEmail() != "")usuario.setEmail(usuarioActualizado.getEmail());
            if(usuarioActualizado.getPassword() != "")usuario.setPassword(encoder.encode(usuarioActualizado.getPassword()));
            // Puedes continuar actualizando otros campos según sea necesario
            
            // Guardar el usuario actualizado
            userRepository.save(usuario);

            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
