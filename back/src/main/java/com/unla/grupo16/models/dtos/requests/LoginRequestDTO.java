package com.unla.grupo16.models.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO para login de usuario (autenticación con email y contraseña)")
public record LoginRequestDTO(
        @Schema(
                description = "Email del usuario. Debe ser una dirección válida.",
                example = "usuario@correo.com",
                required = true
        )
        @Email(message = "Debe ser un email válido")
        @NotBlank(message = "El email no puede estar vacío")
        String email,
        @Schema(
                description = "Contraseña del usuario",
                example = "1234password",
                required = true
        )
        @NotBlank(message = "La contraseña no puede estar vacía")
        String password
        ) {

}
