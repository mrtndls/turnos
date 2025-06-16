package com.unla.grupo16.configurations.mapper;

import org.springframework.stereotype.Component;

import com.unla.grupo16.models.dtos.responses.ClienteAdminDTO;
import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.UserEntity;

@Component
public class ClienteMapper {

    public ClienteAdminDTO toDTO(UserEntity user) {
        if (!(user.getPersona() instanceof Cliente)) {
            throw new IllegalArgumentException("La persona asociada no es un Cliente");
        }

        Cliente cliente = (Cliente) user.getPersona();

        boolean tieneTurnosActivos = cliente.getTurnos().stream()
                .anyMatch(turno -> !turno.isDisponible());

        return ClienteAdminDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .dni(cliente.getDni())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .tieneTurnosActivos(tieneTurnosActivos)
                .clienteActivo(user.isActivo())
                .build();
    }
}
