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
import com.unla.grupo16.models.dtos.responses.ServicioResponseDTO;
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.dtos.responses.UbicacionResponseDTO;
import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.models.entities.UserEntity;
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
    private final ServicioMapper servicioMapper;
    private final UbicacionMapper ubicacionMapper;

    public TurnoRestController(
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

    // 1. Obtener servicios disponibles
    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        List<Servicio> servicios = servicioRepository.findAll();
        List<ServicioResponseDTO> dtoList = servicioMapper.toDTOList(servicios);
        return ResponseEntity.ok(dtoList);
    }

    // 2. Obtener ubicaciones por servicio
    @GetMapping("/servicios/{servicioId}/ubicaciones")
    public ResponseEntity<List<UbicacionResponseDTO>> listarUbicaciones(@PathVariable Integer servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        List<UbicacionResponseDTO> ubicaciones = ubicacionMapper.toDTOList(servicio.getUbicaciones());
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

        Cliente cliente = getClienteAutenticado(principal);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Perfil de cliente requerido"));
        }

        //dto.setIdCliente(cliente.getId());
        Map<String, Object> datos = new HashMap<>();
        servicioRepository.findById(dto.getIdServicio())
                .ifPresent(s -> datos.put("servicio", servicioMapper.toDTO(s)));

        ubicacionRepository.findById(dto.getIdUbicacion())
                .ifPresent(u -> datos.put("ubicacion", ubicacionMapper.toDTO(u)));

        datos.put("fecha", dto.getFecha());
        datos.put("hora", dto.getHora());

        return ResponseEntity.ok(datos);
    }

    // 6. Crear el turno
    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crearTurno(
            @RequestBody TurnoRequestDTO turnoRequestDTO,
            Principal principal) {

        // Obtener email del usuario autenticado (desde el token/sesion)
        String email = principal.getName();

        // Buscar UserEntity con ese email para validar que es cliente y obtener cliente
        UserEntity user = userRepo.findByEmailWithPersona(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no encontrado"));

        if (!(user.getPersona() instanceof Cliente cliente)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil de cliente requerido");
        }

        turnoRequestDTO.setIdCliente(cliente.getId());

        try {
            TurnoResponseDTO turno = turnoService.crearTurno(turnoRequestDTO, email);
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
    /*private Cliente obtenerClienteDesdePrincipal(Principal principal) {
        return userRepo.findByEmailConPersona(principal.getName())
                .map(user -> {
                    Object persona = Hibernate.unproxy(user.getPersona());
                    return (persona instanceof Cliente cliente) ? cliente : null;
                }).orElse(null);
    }*/
    public Cliente getClienteAutenticado(Principal principal) {
        return userRepo.findByEmailConPersona(principal.getName())
                .map(user -> (Cliente) Hibernate.unproxy(user.getPersona()))
                .filter(Cliente.class::isInstance)
                .map(Cliente.class::cast)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Pe rfil de cliente requerido"));
    }

    ////////////////////
    /// para dashboard obtener turnos del cliente
    
    @GetMapping("/mis-turnos")
    public ResponseEntity<List<TurnoResponseDTO>> getTurnosDelCliente(Principal principal) {
        String email = principal.getName();

        UserEntity user = userRepo.findByEmailWithPersona(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no encontrado"));

        if (!(user.getPersona() instanceof Cliente cliente)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil de cliente requerido");
        }

        List<TurnoResponseDTO> turnos = turnoService.obtenerTurnosPorCliente(cliente.getId());
        return ResponseEntity.ok(turnos);
    }

}
