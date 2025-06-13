package com.unla.grupo16.configurations.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.unla.grupo16.models.dtos.responses.ServicioResponseDTO;
import com.unla.grupo16.models.entities.Servicio;

@Component
public class ServicioMapper {

    private final ModelMapper modelMapper;

    public ServicioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ServicioResponseDTO toDTO(Servicio servicio) {
        return modelMapper.map(servicio, ServicioResponseDTO.class);
    }

    public List<ServicioResponseDTO> toDTOList(List<Servicio> servicios) {
        return servicios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
