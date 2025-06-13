package com.unla.grupo16.models.dtos.responses;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TurnoPreviewDTO {
    private Integer idServicio;
    private Integer idUbicacion;
    private LocalDate fecha;
    private LocalTime hora;
}
