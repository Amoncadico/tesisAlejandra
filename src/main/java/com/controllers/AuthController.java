package com.controllers;

import java.time.LocalDate;
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
import com.models.Foro;
import com.models.Role;
import com.models.User;
import com.payload.request.CreatePatientRequest;
import com.payload.request.LoginRequest;
import com.payload.request.SignupRequest;
import com.payload.response.MessageResponse;
import com.repository.ForoRepository;
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
  ForoRepository foroRepository;

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

    // Además de enviar la cookie, devolver el token en el body para que el
    // frontend pueda usar Authorization: Bearer <token> y evitar problemas CORS
    String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
      .body(new com.payload.response.UserInfoResponse(userDetails.getId(),
                     userDetails.getUsername(),
                     roles,
                     jwt));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    User user = new User(signUpRequest.getUsername(),
                         encoder.encode(signUpRequest.getPassword()));

    // Si el request incluye una foto, asignarla al usuario
    if (signUpRequest.getFoto() != null && !signUpRequest.getFoto().isBlank()) {
      user.setFoto(signUpRequest.getFoto());
    }

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_PROFESIONAL)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
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

    // Extraer el RUT sin puntos y sin dígito verificador para la contraseña
    String rutSinFormato = createPatientRequest.getRut()
        .replace(".", "")
        .replace("-", "");
    // Remover el último dígito (dígito verificador)
    String password = rutSinFormato.substring(0, rutSinFormato.length() - 1);

    // Crear el nuevo paciente
    User patient = new User(
        createPatientRequest.getUsername(),
        encoder.encode(password)
    );
    
    patient.setRut(createPatientRequest.getRut());
    patient.setLesion(createPatientRequest.getLesion());
    patient.setFechaNacimiento(createPatientRequest.getFechaNacimiento());
    
    if (createPatientRequest.getFoto() != null) {
        patient.setFoto(createPatientRequest.getFoto());
    }
    
    patient.setFechaRegistro(LocalDate.now());

    // Asignar el rol de paciente
    Role patientRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    Set<Role> roles = new HashSet<>();
    roles.add(patientRole);
    patient.setRoles(roles);

    // Asignar el profesional al paciente
    patient.setProfesionalAsignado(profesional);

    // Guardar el paciente
    User patientGuardado = userRepository.save(patient);

    // Crear automáticamente el foro entre el profesional y el paciente
    Foro foro = new Foro(profesional, patientGuardado);
    foroRepository.save(foro);

    return ResponseEntity.ok(new MessageResponse("Patient profile and forum created successfully!"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }

}
