package com.unla.grupo16.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.unla.grupo16.configurations.mapper.TurnoMapper;
import com.unla.grupo16.exception.DisponibilidadNoEncontradaException;
import com.unla.grupo16.exception.NegocioException;
import com.unla.grupo16.exception.RecursoNoEncontradoException;
import com.unla.grupo16.models.dtos.requests.TurnoRequestDTO;
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.Disponibilidad;
import com.unla.grupo16.models.entities.Empleado;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.models.entities.Turno;
import com.unla.grupo16.models.entities.UserEntity;
import com.unla.grupo16.repositories.IClienteRepository;
import com.unla.grupo16.repositories.IDisponibilidadRepository;
import com.unla.grupo16.repositories.IEmpleadoRepository;
import com.unla.grupo16.repositories.IServicioRepository;
import com.unla.grupo16.repositories.ITurnoRepository;
import com.unla.grupo16.repositories.IUserRepository;
import com.unla.grupo16.services.interfaces.ITurnoService;

import jakarta.mail.MessagingException;

@Service
public class TurnoServiceImpl implements ITurnoService {

    private final ITurnoRepository turnoRepository;
    private final IEmpleadoRepository empleadoRepository;
    private final EmailServiceImpl emailService;
    private final IServicioRepository servicioRepository;
    private final IDisponibilidadRepository disponibilidadRepository;
    private final IUserRepository usuarioRepository;
    private final TurnoMapper turnoMapper;

    public TurnoServiceImpl(ITurnoRepository turnoRepository,
            IClienteRepository clienteRepository,
            IEmpleadoRepository empleadoRepository,
            IServicioRepository servicioRepository,
            IDisponibilidadRepository disponibilidadRepository,
            EmailServiceImpl emailService,
            IUserRepository usuarioRepository,
            TurnoMapper turnoMapper) {
        this.turnoRepository = turnoRepository;
        this.empleadoRepository = empleadoRepository;
        this.servicioRepository = servicioRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.emailService = emailService;
        this.usuarioRepository = usuarioRepository;
        this.turnoMapper = turnoMapper;
    }

    @Override
    public TurnoResponseDTO crearTurno(TurnoRequestDTO dto, String username) throws NegocioException {
        validarTurnoRequest(dto);

        // Obtener usuario autenticado
        UserEntity user = usuarioRepository.findByEmailWithPersona(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (!(user.getPersona() instanceof Cliente)) {
            throw new NegocioException("Solo los clientes pueden reservar turnos.");
        }

        Cliente cliente = (Cliente) user.getPersona();

        Servicio servicio = servicioRepository.findById(dto.getIdServicio())
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado"));

        LocalDateTime fechaHora = LocalDateTime.of(dto.getFecha(), dto.getHora());

        Empleado empleadoAsignado = empleadoRepository.findAll().stream()
                .filter(emp -> !turnoRepository.existsByEmpleadoAndFechaHora(emp, fechaHora))
                .findFirst()
                .orElseThrow(() -> new DisponibilidadNoEncontradaException("No hay empleados disponibles para la fecha y hora"));

        Turno turno = turnoMapper.toEntity(dto);
        turno.setCliente(cliente);
        turno.setEmpleado(empleadoAsignado);
        turno.setServicio(servicio);
        turno.setFechaHora(fechaHora);
        turno.setEstado("RESERVADO");
        turno.setObservaciones(dto.getObservaciones());
        turno.setCodigoAnulacion(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        turnoRepository.save(turno);

        enviarEmailConfirmacion(cliente, servicio, dto, turno);

        return turnoMapper.toDTO(turno);
    }

    private void validarTurnoRequest(TurnoRequestDTO dto) throws NegocioException {
        if (dto.getIdServicio() == null) {
            throw new NegocioException("El idServicio es obligatorio");
        }
        if (dto.getFecha() == null) {
            throw new NegocioException("La fecha es obligatoria");
        }
        if (dto.getHora() == null) {
            throw new NegocioException("La hora es obligatoria");
        }
    }

    private void enviarEmailConfirmacion(Cliente cliente, Servicio servicio, TurnoRequestDTO dto, Turno turno) throws NegocioException {
        UserEntity user = usuarioRepository.findByPersona(cliente)
                .orElseThrow(() -> new NegocioException("Usuario del cliente no encontrado"));

        String mensaje = String.format(
                "Hola %s, su turno para el servicio %s ha sido reservado exitosamente para el %s a las %s. Su código de anulación es: %s.",
                cliente.getNombre(), servicio.getNombre(), dto.getFecha(), dto.getHora(), turno.getCodigoAnulacion()
        );

        try {
            emailService.sendEmail(user.getEmail(), "Confirmación de Turno: " + servicio.getNombre(), mensaje);
        } catch (MessagingException e) {
            throw new NegocioException("Error al enviar email de confirmación: " + e.getMessage());
        }
    }

    @Override
    public List<String> getHorariosDisponibles(Integer servicioId, LocalDate fecha) {
        validarEntrada(servicioId, fecha);

        DayOfWeek dia = fecha.getDayOfWeek();
        List<Disponibilidad> disponibilidades = disponibilidadRepository.findByServicios_IdAndDiaSemana(servicioId, dia);

        List<String> todosHorarios = disponibilidades.stream()
                .flatMap(disp -> generarHorarios(disp.getHoraInicio(), disp.getHoraFin()).stream())
                .collect(Collectors.toList());

        Set<String> horariosOcupados = turnoRepository.findByServicioIdAndFecha(servicioId, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay())
                .stream()
                .map(t -> t.getFechaHora().toLocalTime().toString())
                .collect(Collectors.toSet());

        return todosHorarios.stream()
                .filter(h -> !horariosOcupados.contains(h))
                .collect(Collectors.toList());
    }

    private void validarEntrada(Integer servicioId, LocalDate fecha) {
        if (servicioId == null) {
            throw new IllegalArgumentException("El servicio_id no puede ser null");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser null");
        }
    }

    private List<String> generarHorarios(LocalTime inicio, LocalTime fin) {
        List<String> horarios = new ArrayList<>();
        while (!inicio.isAfter(fin.minusMinutes(1))) {
            horarios.add(inicio.toString());
            inicio = inicio.plusMinutes(30);
        }
        return horarios;
    }

    @Override
    public List<LocalDate> obtenerDiasDisponiblesParaServicio(Servicio servicio) {
        Set<DayOfWeek> diasDisponibles = disponibilidadRepository.findByServiciosContaining(servicio).stream()
                .map(Disponibilidad::getDiaSemana)
                .collect(Collectors.toSet());

        LocalDate hoy = LocalDate.now();
        LocalDate fin = hoy.plusDays(30);

        return hoy.datesUntil(fin.plusDays(1))
                .filter(dia -> diasDisponibles.contains(dia.getDayOfWeek()))
                .filter(dia -> !getHorariosDisponibles(servicio.getId(), dia).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<TurnoResponseDTO> traerTodos() {
        return turnoRepository.findAll().stream()
                .map(turnoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelarTurno(Integer turnoId, String codigoAnulacion) throws NegocioException {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado."));

        if (!turno.getCodigoAnulacion().equalsIgnoreCase(codigoAnulacion)) {
            throw new NegocioException("Código de anulación incorrecto.");
        }
        anularTurno(turno);
    }

    @Override
    public void cancelarTurnoPorCodigo(String codigoAnulacion) throws NegocioException {
        Turno turno = turnoRepository.findByCodigoAnulacion(codigoAnulacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado con ese código."));

        anularTurno(turno);
    }

    private void anularTurno(Turno turno) throws NegocioException {
        if ("ANULADO".equalsIgnoreCase(turno.getEstado())) {
            throw new NegocioException("El turno ya está anulado.");
        }
        turno.setEstado("ANULADO");
        turnoRepository.save(turno);
    }

    @Override
    public List<TurnoResponseDTO> obtenerTurnosPorCliente(Integer clienteId) {
        List<Turno> turnos = turnoRepository.findByClienteIdOrderByFechaHoraDesc(clienteId);
        return turnos.stream()
                .map(turnoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }
}
