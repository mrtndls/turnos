package com.unla.grupo16.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.unla.grupo16.configurations.mapper.ServicioMapper;
import com.unla.grupo16.configurations.mapper.UbicacionMapper;
import com.unla.grupo16.exception.NegocioException;
import com.unla.grupo16.models.dtos.requests.TurnoRequestDTO;
import com.unla.grupo16.models.dtos.responses.DisponibilidadResponseDTO;
import com.unla.grupo16.models.dtos.responses.ServicioResponseDTO;
import com.unla.grupo16.models.dtos.responses.TurnoPreviewDTO;
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.dtos.responses.UbicacionResponseDTO;
import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.repositories.IServicioRepository;
import com.unla.grupo16.repositories.IUbicacionRepository;
import com.unla.grupo16.repositories.IUserRepository;
import com.unla.grupo16.services.interfaces.ITurnoService;

@RestController
@RequestMapping("/api/cliente")
@PreAuthorize("hasRole('USER')")
public class ClienteTurnoRestController {

    private final IServicioRepository servicioRepository;
    private final ITurnoService turnoService;
    private final IUbicacionRepository ubicacionRepository;
    private final IUserRepository userRepo;
    private final ServicioMapper servicioMapper;
    private final UbicacionMapper ubicacionMapper;

    public ClienteTurnoRestController(
            IServicioRepository servicioRepository,
            ITurnoService turnoService,
            IUbicacionRepository ubicacionRepository,
            IUserRepository userRepo,
            ServicioMapper servicioMapper,
            UbicacionMapper ubicacionMapper
    ) {
        this.servicioRepository = servicioRepository;
        this.turnoService = turnoService;
        this.ubicacionRepository = ubicacionRepository;
        this.userRepo = userRepo;
        this.servicioMapper = servicioMapper;
        this.ubicacionMapper = ubicacionMapper;
    }

    // listar servicios
    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        List<Servicio> servicios = servicioRepository.findAll();
        return ResponseEntity.ok(servicioMapper.toDTOList(servicios));
    }

    // listar ubicaciones por servicio
    @GetMapping("/servicios/{servicioId}/ubicaciones")
    public ResponseEntity<List<UbicacionResponseDTO>> listarUbicaciones(@PathVariable Integer servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        return ResponseEntity.ok(ubicacionMapper.toDTOList(servicio.getUbicaciones()));
    }

    // fechas disponibles para turno
    @GetMapping("/servicios/{servicioId}/dias-disponibles")
    public ResponseEntity<List<LocalDate>> diasDisponibles(@PathVariable Integer servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        List<LocalDate> dias = turnoService.obtenerDiasDisponiblesParaServicio(servicio);
        return ResponseEntity.ok(dias);
    }

    // horarios disponibles
    @GetMapping("/servicios/{servicioId}/fechas/{fecha}/horarios")
    public ResponseEntity<List<String>> horariosDisponibles(
            @PathVariable Integer servicioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(turnoService.getHorariosDisponibles(servicioId, fecha));
    }

    // previsualizar turno antes de crear
    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarDatos(@RequestBody TurnoPreviewDTO dto) {
        Map<String, Object> datos = new HashMap<>();

        servicioRepository.findById(dto.getIdServicio())
                .ifPresent(s -> datos.put("servicio", servicioMapper.toDTO(s)));

        ubicacionRepository.findById(dto.getIdUbicacion())
                .ifPresent(u -> datos.put("ubicacion", ubicacionMapper.toDTO(u)));

        datos.put("fecha", dto.getFecha());
        datos.put("hora", dto.getHora());

        return ResponseEntity.ok(datos);
    }

    // crear turno
    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crearTurno(@RequestBody TurnoRequestDTO dto, Principal principal) {
        Cliente cliente = getClienteAutenticado(principal);
        dto.setIdCliente(cliente.getId());

        try {
            return ResponseEntity.ok(turnoService.crearTurno(dto, principal.getName()));
        } catch (NegocioException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    // anular turno con codigo
    @PostMapping("/anular")
    public ResponseEntity<Map<String, String>> anularTurno(@RequestParam String codigoAnulacion) {
        try {
            turnoService.cancelarTurnoPorCodigo(codigoAnulacion);
            return ResponseEntity.ok(Map.of("mensaje", "Turno anulado con éxito"));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // ver turnos del cliente
    @GetMapping("/mis-turnos")
    public ResponseEntity<List<TurnoResponseDTO>> getTurnosDelCliente(Principal principal) {
        Cliente cliente = getClienteAutenticado(principal);
        return ResponseEntity.ok(turnoService.obtenerTurnosPorCliente(cliente.getId()));
    }

    // disponibilidad en dia puntual
    @GetMapping("/servicio/{servicioId}/disponibilidad/{fecha}")
    public ResponseEntity<DisponibilidadResponseDTO> getDisponibilidadPorDia(
            @PathVariable Integer servicioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        DisponibilidadResponseDTO dto = turnoService.obtenerDisponibilidadPorDiaYServicio(fecha, servicioId);
        return ResponseEntity.ok(dto);
    }

    // fechas habilitadas del mes
    @GetMapping("/servicios/{servicioId}/fechas-habilitadas")
    public ResponseEntity<List<String>> obtenerFechasHabilitadas(
            @PathVariable Integer servicioId,
            @RequestParam Integer year,
            @RequestParam Integer month) {

        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        List<String> fechasHabilitadas = turnoService.obtenerFechasHabilitadasPorMes(servicio, year, month);

        return ResponseEntity.ok(fechasHabilitadas);
    }

    // metodo interno : garantiza que solo un cliente pueda operar
    private Cliente getClienteAutenticado(Principal principal) {
        return userRepo.findByEmailConPersona(principal.getName())
                .map(user -> Hibernate.unproxy(user.getPersona()))
                .filter(Cliente.class::isInstance)
                .map(Cliente.class::cast)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil de cliente requerido"));
    }
}
