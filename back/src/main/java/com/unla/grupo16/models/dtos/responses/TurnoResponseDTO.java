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

    private String fecha;             // Fecha separada, ej: "2025-06-13"
    private String hora;              // Hora separada, ej: "09:00"

    private String nombreCliente;
    private String nombreEmpleado;
    private String nombreServicio;

    private String ubicacionDescripcion;

    private String observaciones;
    private String codigoAnulacion;

}
