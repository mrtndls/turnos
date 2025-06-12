package com.unla.grupo16.models.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "Debe ser un email valido")
        @NotBlank(message = "El email no puede estar vacio")
        String email,
        @NotBlank(message = "La contraseña no puede estar vacia")
        String password
        ) {

}
