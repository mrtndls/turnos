package com.unla.grupo16.services.implementations;

import org.springframework.stereotype.Service;

import com.unla.grupo16.repositories.IClienteRepository;
import com.unla.grupo16.repositories.IDisponibilidadRepository;
import com.unla.grupo16.repositories.IEmpleadoRepository;
import com.unla.grupo16.repositories.IServicioRepository;
import com.unla.grupo16.repositories.ITurnoRepository;
import com.unla.grupo16.repositories.IUserRepository;
import com.unla.grupo16.services.interfaces.ITurnoService;

@Service
public class TurnoServiceImpl implements ITurnoService {

    private final ITurnoRepository turnoRepository;
    private final IClienteRepository clienteRepository;
    private final IEmpleadoRepository empleadoRepository;
    private final EmailServiceImpl emailService;
    private final IServicioRepository servicioRepository;
    private final IDisponibilidadRepository disponibilidadRepository;
    private final IUserRepository usuarioRepository;
    private final TurnoMapper turnoMapper;

    public TurnoServiceImpl(ITurnoRepository turnoRepository, IClienteRepository clienteRepository,
            IEmpleadoRepository empleadoRepository, IServicioRepository servicioRepository,
            IDisponibilidadRepository disponibilidadRepository, EmailServiceImpl emailService,
            IUserRepository usuarioRepository, TurnoMapper turnoMapper) {
        this.turnoRepository = turnoRepository;
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.servicioRepository = servicioRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.emailService = emailService;
        this.usuarioRepository = usuarioRepository;
        this.turnoMapper = turnoMapper;
    }

    @Override
    public TurnoResponseDTO crearTurno(TurnoRequestDTO dto) throws NegocioException {

        if (dto.getIdCliente() == null) {
            throw new NegocioException("El idCliente es obligatorio");
        }
        if (dto.getIdServicio() == null) {
            throw new NegocioException("El idServicio es obligatorio");
        }
        if (dto.getFecha() == null) {
            throw new NegocioException("La fecha es obligatoria");
        }
        if (dto.getHora() == null) {
            throw new NegocioException("La hora es obligatoria");
        }

        Turno turno = turnoMapper.toEntity(dto);

        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        Servicio servicio = servicioRepository.findById(dto.getIdServicio())
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado"));

        LocalDateTime fechaHora = LocalDateTime.of(dto.getFecha(), dto.getHora());

        List<Empleado> empleadosDisponibles = empleadoRepository.findAll();

        if (empleadosDisponibles.isEmpty()) {
            throw new DisponibilidadNoEncontradaException(
                    "No hay empleados disponibles para el servicio solicitado");
        }

        Empleado empleadoAsignado = null;
        for (Empleado empleado : empleadosDisponibles) {
            boolean ocupado = turnoRepository.existsByEmpleadoAndFechaHora(empleado, fechaHora);
            if (!ocupado) {
                empleadoAsignado = empleado;
                break;
            }
        }
        if (empleadoAsignado == null) {
            throw new DisponibilidadNoEncontradaException(
                    "No hay empleados disponibles en esa fecha y hora");
        }

        turno.setCliente(cliente);
        turno.setEmpleado(empleadoAsignado);
        turno.setServicio(servicio);
        turno.setFechaHora(fechaHora);
        turno.setEstado("RESERVADO");
        turno.setObservaciones(dto.getObservaciones());
        turno.setCodigoAnulacion(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        turnoRepository.save(turno);

        UserEntity usuarioCliente = usuarioRepository.findByPersona(cliente)
                .orElseThrow(() -> new NegocioException("No se encontro usuario asociado al cliente"));

        String emailCliente = usuarioCliente.getEmail();

        String asunto = "Confirmacion de Turno : " + servicio.getNombre();
        String mensaje = String.format(
                "Hola %s, su turno para el servicio %s ha sido reservado exitosamente para el %s a las %s. Su codigo de anulacion es: %s.",
                cliente.getNombre(), servicio.getNombre(), dto.getFecha(), dto.getHora(), turno.getCodigoAnulacion());

        try {
            emailService.sendEmail(emailCliente, asunto, mensaje);
        } catch (Exception e) {
            throw new NegocioException("Error al enviar el email de confirmacion: " + e.getMessage());
        }

        return turnoMapper.toDTO(turno);
    }

    @Override
    public List<String> getHorariosDisponibles(Integer servicioId, LocalDate fecha) {
        if (servicioId == null) {
            throw new IllegalArgumentException("El servicio_id no puede ser null");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser null");
        }

        DayOfWeek diaSemana = fecha.getDayOfWeek();

        List<Disponibilidad> disponibilidades = disponibilidadRepository.findByServicios_IdAndDiaSemana(
                servicioId,
                diaSemana);

        List<String> todosHorarios = new ArrayList<>();

        for (Disponibilidad disp : disponibilidades) {
            LocalTime horaActual = disp.getHoraInicio();
            while (!horaActual.isAfter(disp.getHoraFin().minusMinutes(1))) {
                todosHorarios.add(horaActual.toString());
                horaActual = horaActual.plusMinutes(30);
            }
        }

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1);

        List<Turno> turnosOcupados = turnoRepository.findByServicioIdAndFecha(servicioId, inicio, fin);

        Set<String> horasOcupadas = turnosOcupados.stream()
                .map(t -> t.getFechaHora().toLocalTime().toString())
                .collect(Collectors.toSet());

        List<String> horariosLibres = todosHorarios.stream()
                .filter(h -> !horasOcupadas.contains(h))
                .collect(Collectors.toList());

        return horariosLibres;
    }

    @Override
    public List<LocalDate> obtenerDiasDisponiblesParaServicio(Servicio servicio) {
        List<LocalDate> diasDisponibles = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        LocalDate hasta = hoy.plusDays(30);

        List<Disponibilidad> disponibilidades = disponibilidadRepository.findByServiciosContaining(servicio);
        Set<DayOfWeek> diasSemanaDisponibles = disponibilidades.stream()
                .map(Disponibilidad::getDiaSemana)
                .collect(Collectors.toSet());

        for (LocalDate fecha = hoy; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            if (diasSemanaDisponibles.contains(fecha.getDayOfWeek())) {
                List<String> horarios = getHorariosDisponibles(servicio.getId(), fecha);
                if (!horarios.isEmpty()) {
                    diasDisponibles.add(fecha);
                }
            }
        }
        return diasDisponibles;
    }

    @Override
    public List<TurnoResponseDTO> traerTodos() {
        List<Turno> turnos = turnoRepository.findAll();
        return turnos.stream()
                .map(turnoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelarTurno(Integer turnoId, String codigoAnulacion) throws NegocioException {
        if (turnoId == null || codigoAnulacion == null || codigoAnulacion.isBlank()) {
            throw new NegocioException("ID del turno y codigo de anulacion son obligatorios.");
        }

        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado."));

        if (!turno.getCodigoAnulacion().equals(codigoAnulacion)) {
            throw new NegocioException("Codigo de anulacion incorrecto.");
        }

        if ("ANULADO".equalsIgnoreCase(turno.getEstado())) {
            throw new NegocioException("El turno ya esta anulado.");
        }

        turno.setEstado("ANULADO");
        turnoRepository.save(turno);
    }

    @Override
    public void cancelarTurnoPorCodigo(String codigoAnulacion) throws NegocioException {
        if (codigoAnulacion == null || codigoAnulacion.isBlank()) {
            throw new NegocioException("Codigo de anulacion es obligatorio.");
        }

        Turno turno = turnoRepository.findByCodigoAnulacion(codigoAnulacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro ningun turno con ese codigo."));

        if ("ANULADO".equalsIgnoreCase(turno.getEstado())) {
            throw new NegocioException("El turno ya esta anulado.");
        }

        turno.setEstado("ANULADO");
        turnoRepository.save(turno);
    }

}
