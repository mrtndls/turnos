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

    private Integer id; // ID del turno creado

    private String fecha;             // ej: "2025-06-13"
    private String hora;              // ej: "09:00"

    private String nombreServicio;
    private String ubicacionDescripcion;
    private String nombreEmpleado;    // puede ser null o "A confirmar"
    private String nombreCliente;

    private String codigoAnulacion;   // importante para anulación
}
