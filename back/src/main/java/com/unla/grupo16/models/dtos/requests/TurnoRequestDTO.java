package com.unla.grupo16.models.dtos.requests;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TurnoRequestDTO {

    //@NotNull(message = "El ID del cliente es obligatorio")
    private Integer idCliente;

    @NotNull(message = "El ID del servicio es obligatorio")
    private Integer idServicio;

    @NotNull(message = "El ID de la ubicacion es obligatorio")
    private Integer idUbicacion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @Size(max = 500, message = "Las observaciones no deben exceder los 500 caracteres")
    private String observaciones;
}