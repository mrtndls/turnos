package com.unla.grupo16.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unla.grupo16.exception.NegocioException;
import com.unla.grupo16.exception.RecursoNoEncontradoException;
import com.unla.grupo16.models.dtos.responses.ClienteAdminDTO;
import com.unla.grupo16.models.dtos.responses.ClientesAdminResponseDTO;
import com.unla.grupo16.models.dtos.responses.TurnoAdminDTO;
import com.unla.grupo16.services.interfaces.IClienteService;
import com.unla.grupo16.services.interfaces.ITurnoService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminClienteRestController {

    private final IClienteService clienteService;
    private final ITurnoService turnoService;

    public AdminClienteRestController(IClienteService clienteService, ITurnoService turnoService) {
        this.clienteService = clienteService;
        this.turnoService = turnoService;
    }

    @GetMapping("/turnos")
    public ResponseEntity<List<TurnoAdminDTO>> listarTurnosNoDisponibles() {
        List<TurnoAdminDTO> turnos = turnoService.obtenerTurnosNoDisponibles();
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/clientes")
    public ResponseEntity<ClientesAdminResponseDTO> listarClientes() {
        ClientesAdminResponseDTO response = clienteService.obtenerClientesActivosYBajaLogica();
        return ResponseEntity.ok(response);
    }

// Dar de baja (soft delete)
    @PutMapping("/{clienteId}/baja")
    public ResponseEntity<String> darDeBajaCliente(@PathVariable Integer clienteId) {
        try {
            clienteService.darDeBajaCliente(clienteId);
            return ResponseEntity.ok("Cliente dado de baja correctamente.");
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado.");
        }
    }

    // Dar de alta (reactivar cliente)
    @PutMapping("/{clienteId}/alta")
    public ResponseEntity<String> darDeAltaCliente(@PathVariable Integer clienteId) {
        try {
            clienteService.darDeAltaCliente(clienteId);
            return ResponseEntity.ok("Cliente reactivado correctamente.");
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado.");
        }
    }

    // Editar cliente
    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteAdminDTO> editarCliente(@PathVariable Integer clienteId,
            @RequestBody ClienteAdminDTO clienteDto) {
        try {
            ClienteAdminDTO clienteActualizado = clienteService.editarCliente(clienteId, clienteDto);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
