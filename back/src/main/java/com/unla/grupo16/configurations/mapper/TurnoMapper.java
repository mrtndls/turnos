package com.unla.grupo16.configurations.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.unla.grupo16.models.dtos.requests.TurnoRequestDTO;
import com.unla.grupo16.models.dtos.responses.TurnoAdminDTO;
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.models.entities.Turno;

@Component
public class TurnoMapper {

    private final ModelMapper modelMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public TurnoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TurnoResponseDTO toDTO(Turno turno) {
        TurnoResponseDTO dto = new TurnoResponseDTO();

        dto.setId(turno.getId());

        LocalDateTime fechaHora = turno.getFechaHora();
        if (fechaHora != null) {
            dto.setFecha(fechaHora.toLocalDate().format(DATE_FORMATTER));
            dto.setHora(fechaHora.toLocalTime().format(TIME_FORMATTER));
        }

        dto.setNombreCliente(turno.getCliente() != null ? turno.getCliente().getNombre() : null);
        dto.setNombreEmpleado(turno.getEmpleado() != null ? turno.getEmpleado().getNombre() : null);
        dto.setNombreServicio(turno.getServicio() != null ? turno.getServicio().getNombre() : null);

        String ubicacionDescripcion = "Sin ubicación";
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
                    .orElse("Sin ubicación");
        }
        dto.setUbicacionDescripcion(ubicacionDescripcion);

        dto.setObservaciones(turno.getObservaciones());
        dto.setCodigoAnulacion(turno.getCodigoAnulacion());

        return dto;
    }

    public Turno toEntity(TurnoRequestDTO dto) {
        Turno turno = new Turno();
        turno.setFechaHora(LocalDateTime.of(dto.getFecha(), dto.getHora()));
        turno.setDisponible(false); // Por defecto reservado
        return turno;
    }

    public TurnoAdminDTO toAdminDTO(Turno turno) {
        TurnoAdminDTO dto = new TurnoAdminDTO();

        dto.setId(turno.getId());
        dto.setFechaHora(turno.getFechaHora());
        dto.setObservaciones(turno.getObservaciones());
        dto.setCodigoAnulacion(turno.getCodigoAnulacion());
        dto.setDisponible(turno.isDisponible());

        if (turno.getFechaHora() != null) {
            dto.setFecha(turno.getFechaHora().toLocalDate().format(DATE_FORMATTER));
            dto.setHora(turno.getFechaHora().toLocalTime().format(TIME_FORMATTER));
        }

        dto.setNombreCliente(turno.getCliente() != null ? turno.getCliente().getNombre() : null);
        dto.setNombreEmpleado(turno.getEmpleado() != null ? turno.getEmpleado().getNombre() : null);
        dto.setNombreServicio(turno.getServicio() != null ? turno.getServicio().getNombre() : null);

        String ubicacionDescripcion = "Sin ubicación";
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
                    .orElse("Sin ubicación");
        }
        dto.setUbicacionDescripcion(ubicacionDescripcion);

        return dto;
    }
}
