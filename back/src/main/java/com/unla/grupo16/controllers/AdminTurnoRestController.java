package com.unla.grupo16.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.services.interfaces.ITurnoService;

@RestController
@RequestMapping("/api/admin/turnos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTurnoRestController {

    private final ITurnoService turnoService;

    public AdminTurnoRestController(ITurnoService turnoService) {
        this.turnoService = turnoService;
    }

@GetMapping(value = {"", "/"})
public ResponseEntity<List<TurnoResponseDTO>> getTodos() {
    System.out.println("Solicitando turnos para ADMIN");
    return ResponseEntity.ok(turnoService.traerTodos());
}


    // Otros métodos de administración podrían ir acá más adelante
}
