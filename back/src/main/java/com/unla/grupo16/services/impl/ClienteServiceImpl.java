package com.unla.grupo16.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.unla.grupo16.configurations.mapper.ClienteMapper;
import com.unla.grupo16.exception.NegocioException;
import com.unla.grupo16.exception.RecursoNoEncontradoException;
import com.unla.grupo16.models.dtos.responses.ClienteAdminDTO;
import com.unla.grupo16.models.dtos.responses.ClientesAdminResponseDTO;
import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.UserEntity;
import com.unla.grupo16.repositories.IClienteRepository;
import com.unla.grupo16.repositories.IUserRepository;
import com.unla.grupo16.services.interfaces.IClienteService;

@Service
public class ClienteServiceImpl implements IClienteService {

    private final IUserRepository userRepository;
    private final IClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteServiceImpl(IUserRepository userRepository, IClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.userRepository = userRepository;
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    public ClientesAdminResponseDTO obtenerClientesActivosYBajaLogica() {
        List<UserEntity> usuarios = userRepository.findAllClientesConUsuarioIncluyendoBaja();

        List<ClienteAdminDTO> activos = usuarios.stream()
                .filter(UserEntity::isActivo)
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());

        List<ClienteAdminDTO> bajaLogica = usuarios.stream()
                .filter(user -> !user.isActivo())
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());

        return ClientesAdminResponseDTO.builder()
                .clientesActivos(activos)
                .clientesBajaLogica(bajaLogica)
                .build();
    }

    @Override
    public void darDeBajaCliente(Integer clienteId) throws NegocioException, RecursoNoEncontradoException {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        boolean tieneTurnosActivos = cliente.getTurnos().stream()
                .anyMatch(turno -> !turno.isDisponible());

        if (tieneTurnosActivos) {
            throw new NegocioException("No se puede dar de baja un cliente con turnos activos");
        }

        UserEntity user = userRepository.findByPersona(cliente)
                .orElseThrow(() -> new NegocioException("Usuario asociado al cliente no encontrado"));

        user.setActivo(false);
        userRepository.save(user);
    }

    @Override
    public void darDeAltaCliente(Integer clienteId) throws RecursoNoEncontradoException {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        UserEntity user = userRepository.findByPersona(cliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario asociado al cliente no encontrado"));

        user.setActivo(true);
        userRepository.save(user);
    }

    @Override
    public ClienteAdminDTO editarCliente(Integer clienteId, ClienteAdminDTO clienteDto) throws RecursoNoEncontradoException {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        cliente.setNombre(clienteDto.getNombre());
        cliente.setApellido(clienteDto.getApellido());
        cliente.setDni(clienteDto.getDni());

        UserEntity user = userRepository.findByPersona(cliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario asociado al cliente no encontrado"));

        user.setEmail(clienteDto.getEmail());

        clienteRepository.save(cliente);
        userRepository.save(user);

        return clienteMapper.toDTO(user);
    }
}
