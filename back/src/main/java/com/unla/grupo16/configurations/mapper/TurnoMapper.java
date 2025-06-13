package com.unla.grupo16.configurations.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.unla.grupo16.models.dtos.requests.TurnoRequestDTO;
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.entities.Turno;
import com.unla.grupo16.models.entities.Ubicacion;

@Component
public class TurnoMapper {

    private final ModelMapper modelMapper;

    public TurnoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TurnoResponseDTO toDTO(Turno turno) {
        TurnoResponseDTO dto = modelMapper.map(turno, TurnoResponseDTO.class);

        LocalDateTime fechaHora = turno.getFechaHora();

        // Separar fecha y hora para el frontend
        dto.setFecha(fechaHora.toLocalDate().toString()); // ej: "2025-06-13"
        dto.setHora(fechaHora.toLocalTime().toString());  // ej: "09:00"

        // Esto es opcional si todavía lo usás en otro lado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dto.setFechaHora(fechaHora.format(formatter));

        dto.setNombreCliente(turno.getCliente().getNombre());
        dto.setNombreEmpleado(turno.getEmpleado().getNombre());
        dto.setNombreServicio(turno.getServicio().getNombre());

        // Ubicación
        Optional<Ubicacion> ubiOpt = turno.getServicio().getUbicaciones().stream().findFirst();
        String ubicacionDescripcion = ubiOpt.map(ubicacion -> {
            String direccion = ubicacion.getDireccion();
            String localidad = (ubicacion.getLocalidad() != null) ? ubicacion.getLocalidad().getNombre() : "Sin localidad";
            return direccion + " (" + localidad + ")";
        }).orElse("Sin ubicación");

        dto.setUbicacionDescripcion(ubicacionDescripcion);

        return dto;
    }

    public Turno toEntity(TurnoRequestDTO dto) {
        Turno turno = new Turno();
        turno.setFechaHora(LocalDateTime.of(dto.getFecha(), dto.getHora()));
        turno.setEstado("PENDIENTE");
        return turno;
    }
}
