package com.unla.grupo16.models.dtos.responses;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteAdminDTO {

    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean tieneTurnosActivos;
    private boolean clienteActivo;

}
