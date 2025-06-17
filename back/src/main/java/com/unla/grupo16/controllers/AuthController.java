package com.unla.grupo16.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unla.grupo16.configurations.security.jwt.JwtUtil;
import com.unla.grupo16.exception.AutenticacionException;
import com.unla.grupo16.exception.RecursoNoEncontradoException;
import com.unla.grupo16.exception.UsuarioDeshabilitadoException;
import com.unla.grupo16.models.dtos.requests.LoginRequestDTO;
import com.unla.grupo16.models.dtos.responses.LoginResponseDto;
import com.unla.grupo16.models.entities.Persona;
import com.unla.grupo16.models.entities.UserEntity;
import com.unla.grupo16.repositories.IUserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Autenticación", description = "Autenticación de usuarios mediante JWT")
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

    @Operation(summary = "Autenticar usuario y generar JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "Usuario deshabilitado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error inesperado")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            var authenticatedUser = (UserDetails) authentication.getPrincipal();

            UserEntity user = userRepo.findByEmailWithPersona(authenticatedUser.getUsername())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

            if (!user.isEnabled()) {
                throw new UsuarioDeshabilitadoException("El usuario esta deshabilitado");
            }

            String token = jwtUtil.generateToken(authenticatedUser);

            String rol = user.getRoleEntities().stream()
                    .findFirst()
                    .map(role -> role.getType().name())
                    .orElse("USER");

            Persona persona = user.getPersona();
            Integer id = persona != null ? persona.getIdPersona() : user.getId();
            String nombreCompleto = persona != null ? persona.getNombreCompleto() : user.getEmail();

            var response = new LoginResponseDto(
                    token,
                    user.getEmail(),
                    rol,
                    id,
                    nombreCompleto
            );

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            throw new AutenticacionException("Credenciales invalidas");
        }
    }
}
