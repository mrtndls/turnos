package com.unla.grupo16.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.unla.grupo16.configurations.security.jwt.JwtUtil;
import com.unla.grupo16.models.dtos.requests.LoginRequest;
import com.unla.grupo16.models.dtos.responses.UserLoginResponseDto;
import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.Empleado;
import com.unla.grupo16.models.entities.UserEntity;
import com.unla.grupo16.repositories.IUserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final IUserRepository userRepo;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, IUserRepository userRepo) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );

            var userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Obtenemos el UserEntity completo con su persona y roles
            UserEntity user = userRepo.findByEmailWithPersona(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            // Obtener el primer rol del usuario
            String rol = user.getRoleEntities().stream()
                    .findFirst()
                    .map(role -> role.getType().name()) // "ADMIN" o "USER"
                    .orElse("USER");

            Integer id = null;
            String nombreCompleto = "";

            if (user.getPersona() instanceof Cliente cliente) {
                id = cliente.getId();
                nombreCompleto = cliente.getNombre() + " " + cliente.getApellido();
            } else if (user.getPersona() instanceof Empleado empleado) {
                id = empleado.getId();
                nombreCompleto = empleado.getNombre() + " " + empleado.getApellido();
            }

            var response = new UserLoginResponseDto(token, user.getEmail(), rol, id, nombreCompleto);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserLoginResponseDto> getPerfil(Principal principal) {
        UserEntity user = userRepo.findByEmailWithPersona(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        String rol = user.getRoleEntities().stream()
                .findFirst()
                .map(r -> r.getType().name()) // ADMIN o USER
                .orElse("USER");

        Integer id = null;
        String nombreCompleto = "";

        if (user.getPersona() instanceof Cliente cliente) {
            id = cliente.getId();
            nombreCompleto = cliente.getNombre() + " " + cliente.getApellido();
        } else if (user.getPersona() instanceof Empleado empleado) {
            id = empleado.getId();
            nombreCompleto = empleado.getNombre() + " " + empleado.getApellido();
        }

        return ResponseEntity.ok(new UserLoginResponseDto(null, user.getEmail(), rol, id, nombreCompleto));
    }

}
