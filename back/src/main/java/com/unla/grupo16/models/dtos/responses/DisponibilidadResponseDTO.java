package com.unla.grupo16.models.dtos.responses;

import java.time.LocalDate;
import java.time.LocalTime;
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
public class DisponibilidadResponseDTO {

    private LocalDate fecha;
    private boolean activo;
    private List<HorarioDTO> horarios;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HorarioDTO {

        private LocalTime horaInicio;
        private LocalTime horaFin;
    }
}
