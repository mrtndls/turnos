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

import com.unla.grupo16.exception.NegocioException;
import com.unla.grupo16.models.dtos.requests.TurnoRequestDTO;
import com.unla.grupo16.models.dtos.responses.ServicioResponseDTO;
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.dtos.responses.UbicacionResponseDTO;
import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.repositories.IServicioRepository;
import com.unla.grupo16.repositories.IUbicacionRepository;
import com.unla.grupo16.repositories.IUserRepository;
import com.unla.grupo16.services.interfaces.ITurnoService;

@RestController
@RequestMapping("/api/turnos")
@PreAuthorize("hasRole('USER','ADMIN')")
public class TurnoRestController {

    private final IServicioRepository servicioRepository;
    private final ITurnoService turnoService;
    private final IUbicacionRepository ubicacionRepository;
    private final IUserRepository userRepo;

    public TurnoRestController(
            IServicioRepository servicioRepository,
            ITurnoService turnoService,
            IUbicacionRepository ubicacionRepository,
            IUserRepository userRepo) {
        this.servicioRepository = servicioRepository;
        this.turnoService = turnoService;
        this.ubicacionRepository = ubicacionRepository;
        this.userRepo = userRepo;
    }

    // 1. Obtener servicios disponibles
    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        List<ServicioResponseDTO> servicios = servicioRepository.findAll()
                .stream()
                .map(ServicioResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(servicios);
    }

    // 2. Obtener ubicaciones por servicio
    @GetMapping("/servicios/{servicioId}/ubicaciones")
    public ResponseEntity<List<UbicacionResponseDTO>> listarUbicaciones(@PathVariable Integer servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        List<UbicacionResponseDTO> ubicaciones = servicio.getUbicaciones()
                .stream()
                .map(UbicacionResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(ubicaciones);
    }

    // 3. Obtener días disponibles para un servicio
    @GetMapping("/servicios/{servicioId}/dias-disponibles")
    public ResponseEntity<List<LocalDate>> diasDisponibles(@PathVariable Integer servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        List<LocalDate> dias = turnoService.obtenerDiasDisponiblesParaServicio(servicio);
        return ResponseEntity.ok(dias);
    }

    // 4. Obtener horarios disponibles para un día
    @GetMapping("/servicios/{servicioId}/fechas/{fecha}/horarios")
    public ResponseEntity<List<String>> horariosDisponibles(
            @PathVariable Integer servicioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<String> horarios = turnoService.getHorariosDisponibles(servicioId, fecha);
        return ResponseEntity.ok(horarios);
    }

    // 5. Confirmar datos antes de crear (opcional)
    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarDatos(
            @RequestBody TurnoRequestDTO dto,
            Principal principal) {

        Cliente cliente = obtenerClienteDesdePrincipal(principal);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Perfil de cliente requerido"));
        }

        dto.setIdCliente(cliente.getId());

        Map<String, Object> datos = new HashMap<>();
        servicioRepository.findById(dto.getIdServicio()).ifPresent(s -> datos.put("servicio", ServicioResponseDTO.fromEntity(s)));
        ubicacionRepository.findById(dto.getIdUbicacion()).ifPresent(u -> datos.put("ubicacion", UbicacionResponseDTO.fromEntity(u)));
        datos.put("fecha", dto.getFecha());
        datos.put("hora", dto.getHora());

        return ResponseEntity.ok(datos);
    }

    // 6. Crear el turno
    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crearTurno(
            @RequestBody TurnoRequestDTO turnoRequestDTO,
            Principal principal) {

        Cliente cliente = obtenerClienteDesdePrincipal(principal);
        if (cliente == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil de cliente requerido");
        }

        turnoRequestDTO.setIdCliente(cliente.getId());

        try {
            TurnoResponseDTO turno = turnoService.crearTurno(turnoRequestDTO);
            return ResponseEntity.ok(turno);
        } catch (NegocioException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    // 7. Anular turno
    @PostMapping("/anular")
    public ResponseEntity<Map<String, String>> anularTurno(@RequestParam String codigoAnulacion) {
        try {
            turnoService.cancelarTurnoPorCodigo(codigoAnulacion);
            return ResponseEntity.ok(Map.of("mensaje", "Turno anulado con éxito"));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // === Helpers ===
    private Cliente obtenerClienteDesdePrincipal(Principal principal) {
        return userRepo.findByEmailConPersona(principal.getName())
                .map(user -> {
                    Object persona = Hibernate.unproxy(user.getPersona());
                    return (persona instanceof Cliente cliente) ? cliente : null;
                }).orElse(null);
    }
}
