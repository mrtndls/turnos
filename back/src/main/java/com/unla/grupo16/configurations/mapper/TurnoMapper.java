package com.unla.grupo16.configurations.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.unla.grupo16.models.dtos.requests.TurnoRequestDTO;
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.models.entities.Turno;

@Component
public class TurnoMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public TurnoResponseDTO toDTO(Turno turno) {
        TurnoResponseDTO dto = new TurnoResponseDTO();

        dto.setId(turno.getId());

        // fecha y hora
        LocalDateTime fechaHora = turno.getFechaHora();
        if (fechaHora != null) {
            dto.setFecha(fechaHora.toLocalDate().format(DATE_FORMATTER));
            dto.setHora(fechaHora.toLocalTime().format(TIME_FORMATTER));
        }

        // servicio
        dto.setNombreServicio(turno.getServicio() != null ? turno.getServicio().getNombre() : null);

        // empleado
        dto.setNombreEmpleado(turno.getEmpleado() != null
                ? turno.getEmpleado().getNombre()
                : "A confirmar");

        dto.setNombreCliente(turno.getCliente() != null ? turno.getCliente().getNombre() : null);

        // ubicacion
        String ubicacionDescripcion = "Sin ubicacion";
        Servicio servicio = turno.getServicio();
        if (servicio != null && servicio.getUbicaciones() != null && !servicio.getUbicaciones().isEmpty()) {
            ubicacionDescripcion = servicio.getUbicaciones().stream()
                    .findFirst()
                    .map(ubicacion -> {
                        String direccion = ubicacion.getDireccion();
                        String localidad = (ubicacion.getLocalidad() != null)
                                ? ubicacion.getLocalidad().getNombre()
                                : "Sin localidad";
                        return direccion + " (" + localidad + ")";
                    })
                    .orElse("Sin ubicacion");
        }
        dto.setUbicacionDescripcion(ubicacionDescripcion);

        // otros campos
        dto.setObservaciones(turno.getObservaciones());
        dto.setCodigoAnulacion(turno.getCodigoAnulacion());

        return dto;
    }

    public Turno toEntity(TurnoRequestDTO dto) {
        Turno turno = new Turno();
        turno.setFechaHora(LocalDateTime.of(dto.getFecha(), dto.getHora()));
        turno.setObservaciones(dto.getObservaciones());
        turno.setDisponible(false); // reservado por defecto
        return turno;
    }

}
