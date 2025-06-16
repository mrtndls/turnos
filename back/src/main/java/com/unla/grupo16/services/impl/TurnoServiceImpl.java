package com.unla.grupo16.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
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
import com.unla.grupo16.models.dtos.responses.DisponibilidadResponseDTO;
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

        // para solo comparar hasta minutos
        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);
        if (fechaHora.isBefore(ahora)) {
            throw new NegocioException("No se pueden reservar turnos en fechas u horarios pasados.");
        }

        Empleado empleadoAsignado = empleadoRepository.findAll().stream()
                .filter(emp -> !turnoRepository.existsByEmpleadoAndFechaHoraAndDisponibleFalse(emp, fechaHora))
                .findFirst()
                .orElseThrow(() -> new DisponibilidadNoEncontradaException("No hay empleados disponibles para la fecha y hora"));

        Turno turno = turnoMapper.toEntity(dto);
        turno.setCliente(cliente);
        turno.setEmpleado(empleadoAsignado);
        turno.setServicio(servicio);
        turno.setFechaHora(fechaHora);
        //turno.setEstado("RESERVADO");
        turno.setDisponible(false);
        turno.setObservaciones(dto.getObservaciones());
        turno.setCodigoAnulacion(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        turnoRepository.save(turno);

        enviarEmailConfirmacion(user.getEmail(), cliente, servicio, dto, turno);

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

    private void enviarEmailConfirmacion(String emailCliente, Cliente cliente, Servicio servicio, TurnoRequestDTO dto, Turno turno) throws NegocioException {
        String mensaje = String.format(
                "Hola %s, su turno para el servicio %s ha sido reservado exitosamente para el %s a las %s. Su código de anulación es: %s.",
                cliente.getNombre(), servicio.getNombre(), dto.getFecha(), dto.getHora(), turno.getCodigoAnulacion()
        );

        try {
            emailService.sendEmail(emailCliente, "Confirmación de Turno: " + servicio.getNombre(), mensaje);
        } catch (MessagingException e) {
            throw new NegocioException("Error al enviar email de confirmación: " + e.getMessage());
        }
    }

    @Override
    public List<String> getHorariosDisponibles(Integer servicioId, LocalDate fecha) {
        validarEntrada(servicioId, fecha);

        if (fecha.isBefore(LocalDate.now())) {
            return Collections.emptyList(); // O lanzar excepción, según prefieras
        }

        DayOfWeek dia = fecha.getDayOfWeek();
        List<Disponibilidad> disponibilidades = disponibilidadRepository.findByServicios_IdAndDiaSemana(servicioId, dia);

        List<String> todosHorarios = disponibilidades.stream()
                .flatMap(disp -> generarHorarios(disp.getHoraInicio(), disp.getHoraFin()).stream())
                .collect(Collectors.toList());

        Set<String> horariosOcupados = turnoRepository.findByServicioIdAndFecha(servicioId, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay())
                .stream()
                .map(t -> t.getFechaHora().toLocalTime().toString())
                .collect(Collectors.toSet());

        LocalTime ahora = fecha.equals(LocalDate.now()) ? LocalTime.now().withSecond(0).withNano(0) : null;

        return todosHorarios.stream()
                .filter(h -> !horariosOcupados.contains(h))
                .filter(h -> {
                    if (ahora == null) {
                        return true;
                    }
                    return LocalTime.parse(h).isAfter(ahora);
                })
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
        if (turno.isDisponible()) {
            throw new NegocioException("El turno ya fue anulado o ya está disponible.");
        }

        turno.setDisponible(true); // Se vuelve a marcar como disponible
        turnoRepository.save(turno);
    }

    // Solo se devuelven los turnos que están reservados (disponible = false), es decir, activos para el cliente.
    @Override
    public List<TurnoResponseDTO> obtenerTurnosPorCliente(Integer clienteId) {
        List<Turno> turnos = turnoRepository.findByClienteIdAndDisponibleFalse(clienteId); // solo los turnos reservados
        return turnos.stream()
                .map(turnoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TurnoResponseDTO> traerSoloTurnosReservados() {
        List<Turno> turnos = turnoRepository.findByDisponibleFalseOrderByFechaHoraAsc();
        return turnos.stream()
                .map(turnoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DisponibilidadResponseDTO obtenerDisponibilidadPorDiaYServicio(LocalDate fecha, Integer servicioId) {
        DayOfWeek diaSemana = fecha.getDayOfWeek();

        // Buscar disponibilidades para el día y servicio
        List<Disponibilidad> disponibilidades = disponibilidadRepository.findByDiaSemanaAndServiciosId(diaSemana, servicioId);

        // Verificar si la fecha está dentro del rango permitido (por ej, no días pasados)
        boolean activo = !disponibilidades.isEmpty() && !fecha.isBefore(LocalDate.now());

        // Hora actual solo si la fecha es hoy
        LocalTime ahora = fecha.isEqual(LocalDate.now()) ? LocalTime.now() : null;

        // Construir lista de horarios filtrando por hora actual si es hoy
        List<DisponibilidadResponseDTO.HorarioDTO> horarios = disponibilidades.stream()
                .map(d -> {
                    if (ahora != null) {
                        // Si el horario termina antes que ahora, descartarlo
                        if (d.getHoraFin().isBefore(ahora) || d.getHoraFin().equals(ahora)) {
                            return null;
                        }
                        // Si el horario empieza antes que ahora, ajustar inicio a ahora
                        if (d.getHoraInicio().isBefore(ahora)) {
                            return new DisponibilidadResponseDTO.HorarioDTO(ahora.withSecond(0).withNano(0), d.getHoraFin());
                        }
                    }
                    return new DisponibilidadResponseDTO.HorarioDTO(d.getHoraInicio(), d.getHoraFin());
                })
                .filter(horario -> horario != null && !horario.getHoraInicio().isAfter(horario.getHoraFin()))
                .collect(Collectors.toList());

        return DisponibilidadResponseDTO.builder()
                .fecha(fecha)
                .activo(activo)
                .horarios(horarios)
                .build();
    }

    @Override
    public List<LocalDate> obtenerDiasDisponiblesParaServicio(Servicio servicio) {
        // Obtener los días de la semana disponibles como valores de tipo tinyint
        Set<DayOfWeek> diasDisponibles = disponibilidadRepository.findByServiciosContaining(servicio).stream()
                .map(Disponibilidad::getDiaSemana) // getDiaSemana debe devolver DayOfWeek
                .collect(Collectors.toSet());

        LocalDate hoy = LocalDate.now();
        LocalDate fin = hoy.plusDays(30);

        // Filtrar los días que son habilitados para el servicio y que tienen horarios disponibles
        List<LocalDate> fechasFiltradas = hoy.datesUntil(fin.plusDays(1))
                .filter(dia -> diasDisponibles.contains(dia.getDayOfWeek())) // Filtra solo los días habilitados
                //.peek(d -> System.out.println("Pasa filtro día activo: " + d))
                .filter(dia -> !getHorariosDisponibles(servicio.getId(), dia).isEmpty()) // Verifica que haya horarios disponibles
                //.peek(d -> System.out.println("Día con horario disponible: " + d))
                .collect(Collectors.toList());

        // Devolver las fechas filtradas
        return fechasFiltradas;
    }

    @Override
    public List<String> obtenerFechasHabilitadasPorMes(Servicio servicio, int year, int month) {
        // Fecha de inicio: primer día del mes y año dados
        LocalDate inicio = LocalDate.of(year, month, 1);

        // Fecha fin: último día del mes correspondiente
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        // Obtener todas las fechas disponibles para ese servicio (ejemplo: método existente que devuelve List<LocalDate>)
        List<LocalDate> fechasDisponibles = obtenerDiasDisponiblesParaServicio(servicio).stream()
                // Filtrar fechas dentro del rango del mes consultado
                .filter(dia -> !dia.isBefore(inicio) && !dia.isAfter(fin))
                .collect(Collectors.toList());

        // Convertir las fechas filtradas a String en formato ISO (yyyy-MM-dd)
        return fechasDisponibles.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<Turno> obtenerTurnosNoDisponibles() {
        return turnoRepository.findByDisponibleFalseOrderByFechaHoraAsc();
    }

    @Override
    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }
}
