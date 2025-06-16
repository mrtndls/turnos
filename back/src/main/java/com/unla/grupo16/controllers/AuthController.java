package com.unla.grupo16.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.unla.grupo16.configurations.security.jwt.JwtUtil;
import com.unla.grupo16.models.dtos.requests.LoginRequestDTO;
import com.unla.grupo16.models.dtos.responses.ErrorResponseDTO;
import com.unla.grupo16.models.dtos.responses.UserLoginResponseDto;
import com.unla.grupo16.models.entities.Persona;
import com.unla.grupo16.models.entities.UserEntity;
import com.unla.grupo16.repositories.IUserRepository;

import jakarta.validation.Valid;

@RestController // Devuelve respuestas en formato JSON
@RequestMapping("/api/auth") // Prefijo para todas las rutas de este controlador
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final IUserRepository userRepo;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, IUserRepository userRepo) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    /**
     * Endpoint para autenticación de usuarios.
     *
     * @param loginRequest contiene email y contraseña.
     * @return JWT token + info del usuario autenticado o error.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            var authenticatedUser = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(authenticatedUser);

            // Obtener usuario completo con sus roles y persona
            UserEntity user = userRepo.findByEmailWithPersona(authenticatedUser.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            // Verificar si el usuario está habilitado
            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ErrorResponseDTO("El usuario está deshabilitado", 403)
                );
            }

            // Obtener primer rol del usuario (por simplicidad, solo uno)
            String rol = user.getRoleEntities().stream()
                    .findFirst()
                    .map(role -> role.getType().name())
                    .orElse("USER");

            Persona persona = user.getPersona();

            Integer id;
            String nombreCompleto;

            if (persona != null) {
                id = persona.getIdPersona();
                nombreCompleto = persona.getNombreCompleto();
            } else {
                id = user.getId(); // Asumiendo UserEntity tiene getId()
                nombreCompleto = user.getEmail();
            }

            var response = new UserLoginResponseDto(
                    token,
                    user.getEmail(),
                    rol,
                    id,
                    nombreCompleto
            );

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Credenciales inválidas", 401));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Error inesperado", 500));
        }
    }
}
