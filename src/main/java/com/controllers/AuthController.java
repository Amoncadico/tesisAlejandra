package com.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.ERole;
import com.models.Role;
import com.models.User;
import com.payload.request.CreatePatientRequest;
import com.payload.request.LoginRequest;
import com.payload.request.SignupRequest;
import com.payload.response.MessageResponse;
import com.payload.response.UserInfoResponse;
import com.repository.RoleRepository;
import com.repository.UserRepository;
import com.security.jwt.JwtUtils;
import com.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new UserInfoResponse(userDetails.getId(),
                                   userDetails.getUsername(),
                                   userDetails.getEmail(),
                                   roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
                         signUpRequest.getEmail(),
                         encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "vis":
          Role modRole = roleRepository.findByName(ERole.ROLE_PROFESIONAL)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/create-paciente")
  public ResponseEntity<?> createPatientByProfessional(@Valid @RequestBody CreatePatientRequest createPatientRequest) {
    // Obtener el usuario autenticado (el profesional)
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    
    // Verificar que el usuario autenticado sea un profesional o admin
    User profesional = userRepository.findById(userDetails.getId())
        .orElseThrow(() -> new RuntimeException("Error: User not found."));
    
    boolean isProfesionalOrAdmin = profesional.getRoles().stream()
        .anyMatch(role -> role.getName().equals(ERole.ROLE_PROFESIONAL) ||
                          role.getName().equals(ERole.ROLE_ADMIN));
    
    if (!isProfesionalOrAdmin) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Only professionals or admins can create patient profiles!"));
    }

    // Verificar que el username no esté en uso
    if (userRepository.existsByUsername(createPatientRequest.getUsername())) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    // Verificar que el email no esté en uso
    if (userRepository.existsByEmail(createPatientRequest.getEmail())) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Crear el nuevo paciente
    User patient = new User(
        createPatientRequest.getUsername(),
        createPatientRequest.getEmail(),
        encoder.encode(createPatientRequest.getPassword())
    );

    // Asignar el rol de paciente
    Role patientRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    Set<Role> roles = new HashSet<>();
    roles.add(patientRole);
    patient.setRoles(roles);

    // Asignar el profesional al paciente
    patient.setProfesionalAsignado(profesional);

    // Guardar el paciente
    userRepository.save(patient);

    return ResponseEntity.ok(new MessageResponse("Patient profile created successfully and assigned to professional!"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }

}
