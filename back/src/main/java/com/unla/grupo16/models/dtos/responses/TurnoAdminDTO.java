package com.unla.grupo16.models.dtos.responses;

import java.time.LocalDateTime;

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
public class TurnoAdminDTO {

    private Integer id;
    private LocalDateTime fechaHora;
    private String fecha; // formateado como yyyy-MM-dd
    private String hora;  // formateado como HH:mm
    private String observaciones;
    private String codigoAnulacion;
    private boolean disponible;

    private String nombreCliente;
    private String nombreEmpleado;
    private String nombreServicio;
    private String ubicacionDescripcion;
}
