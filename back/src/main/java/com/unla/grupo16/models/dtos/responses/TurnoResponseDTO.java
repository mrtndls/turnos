package com.unla.grupo16.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponseDTO {

    private Integer id;
    private String estado;

    private String fecha; // 👈 NUEVO
    private String hora;  // 👈 NUEVO

    private String fechaHora; // Opcional, podés eliminarlo si no lo usás más

    private String nombreCliente;
    private String nombreEmpleado;
    private String nombreServicio;
    private String ubicacionDescripcion;
    private String observaciones;
    private String codigoAnulacion;
}
