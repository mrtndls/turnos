package com.unla.grupo16.models.dtos.responses;

public record UserLoginResponseDto(
        String token,
        String email,
        String rol,
        Integer id,
        String nombreCompleto) {

}
