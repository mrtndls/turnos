package com.unla.grupo16.models.dtos.responses;

import java.util.List;

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
public class ClientesAdminResponseDTO {

    private List<ClienteAdminDTO> clientesActivos;
    private List<ClienteAdminDTO> clientesBajaLogica;
}
