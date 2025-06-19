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
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.dtos.responses.UbicacionResponseDTO;
import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.repositories.IServicioRepository;
import com.unla.grupo16.repositories.IUbicacionRepository;
import com.unla.grupo16.repositories.IUserRepository;
import com.unla.grupo16.services.interfaces.ITurnoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Turnos Cliente", description = "Operaciones que pueden realizar los clientes autenticados")
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

    @Operation(summary = "Listar todos los servicios disponibles")
    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        List<Servicio> servicios = servicioRepository.findAll();
        return ResponseEntity.ok(servicioMapper.toDTOList(servicios));
    }

    @Operation(summary = "Listar ubicaciones asociadas a un servicio")
    @GetMapping("/servicios/{servicioId}/ubicaciones")
    public ResponseEntity<List<UbicacionResponseDTO>> listarUbicaciones(
            @Parameter(description = "ID del servicio") @PathVariable Integer servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        return ResponseEntity.ok(ubicacionMapper.toDTOList(servicio.getUbicaciones()));
    }

    @Operation(summary = "Obtener los dias disponibles para un servicio")
    @GetMapping("/servicios/{servicioId}/dias-disponibles")
    public ResponseEntity<List<LocalDate>> diasDisponibles(
            @Parameter(description = "ID del servicio") @PathVariable Integer servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        List<LocalDate> dias = turnoService.obtenerDiasDisponiblesParaServicio(servicio);
        return ResponseEntity.ok(dias);
    }

    @Operation(summary = "Obtener horarios disponibles para una fecha y servicio")
    @GetMapping("/servicios/{servicioId}/fechas/{fecha}/horarios")
    public ResponseEntity<List<String>> horariosDisponibles(
            @Parameter(description = "ID del servicio") @PathVariable Integer servicioId,
            @Parameter(description = "Fecha del turno (yyyy-MM-dd)")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(turnoService.getHorariosDisponibles(servicioId, fecha));
    }

    @Operation(summary = "Previsualizar datos del turno antes de confirmarlo")
    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarDatos(@RequestBody TurnoRequestDTO dto) {
        Map<String, Object> datos = new HashMap<>();

        servicioRepository.findById(dto.getIdServicio())
                .ifPresent(s -> datos.put("servicio", servicioMapper.toDTO(s)));

        ubicacionRepository.findById(dto.getIdUbicacion())
                .ifPresent(u -> datos.put("ubicacion", ubicacionMapper.toDTO(u)));

        datos.put("fecha", dto.getFecha());
        datos.put("hora", dto.getHora());

        return ResponseEntity.ok(datos);
    }

    @Operation(summary = "Crear un nuevo turno para el cliente autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turno creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crearTurno(@RequestBody TurnoRequestDTO dto, Principal principal) {
        Cliente cliente = getClienteAutenticado(principal);

        try {
            return ResponseEntity.ok(turnoService.crearTurno(dto, cliente, principal.getName()));
        } catch (NegocioException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Operation(summary = "Anular un turno utilizando el codigo de anulacion")
    @PostMapping("/anular")
    public ResponseEntity<Map<String, String>> anularTurno(
            @Parameter(description = "Codigo de anulacion enviado por email") @RequestParam String codigoAnulacion) {
        try {
            turnoService.cancelarTurnoPorCodigo(codigoAnulacion);
            return ResponseEntity.ok(Map.of("mensaje", "Turno anulado con exito"));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Ver turnos del cliente autenticado")
    @GetMapping("/mis-turnos")
    public ResponseEntity<List<TurnoResponseDTO>> getTurnosDelCliente(Principal principal) {
        Cliente cliente = getClienteAutenticado(principal);
        return ResponseEntity.ok(turnoService.obtenerTurnosPorCliente(cliente.getId()));
    }

    @Operation(summary = "Ver disponibilidad de un servicio en una fecha puntual")
    @GetMapping("/servicio/{servicioId}/disponibilidad/{fecha}")
    public ResponseEntity<DisponibilidadResponseDTO> getDisponibilidadPorDia(
            @PathVariable Integer servicioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        DisponibilidadResponseDTO dto = turnoService.obtenerDisponibilidadPorDiaYServicio(fecha, servicioId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Obtener fechas habilitadas de un servicio en un mes especifico")
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

    // Interno
    private Cliente getClienteAutenticado(Principal principal) {
        return userRepo.findByEmailOnlyIfHasPersona(principal.getName())
                .map(user -> Hibernate.unproxy(user.getPersona()))
                .filter(Cliente.class::isInstance)
                .map(Cliente.class::cast)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil de cliente requerido"));
    }
}
