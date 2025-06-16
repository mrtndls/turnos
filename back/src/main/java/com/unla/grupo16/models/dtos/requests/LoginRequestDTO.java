package com.unla.grupo16.models.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// datos que el cliente envia en un POST
public record LoginRequestDTO(
        @Email(message = "Debe ser un email valido")
        @NotBlank(message = "El email no puede estar vacio")
        String email,
        @NotBlank(message = "La contraseña no puede estar vacia")
        String password
        ) {

}
