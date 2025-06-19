package com.unla.grupo16.models.dtos.responses;

// es la rta q se envia al cliente despues del login OK
public record LoginResponseDto(
        String token,
        String email,
        String rol, // List<String> si un usuario puede tener mas de 1 rol
        Integer id,
        String nombreCompleto) {

}
