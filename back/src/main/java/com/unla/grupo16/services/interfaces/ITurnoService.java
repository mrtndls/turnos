package com.unla.grupo16.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.unla.grupo16.exception.NegocioException;
import com.unla.grupo16.models.dtos.requests.TurnoRequestDTO;
import com.unla.grupo16.models.dtos.responses.DisponibilidadResponseDTO;
import com.unla.grupo16.models.dtos.responses.TurnoAdminDTO;
import com.unla.grupo16.models.dtos.responses.TurnoResponseDTO;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.models.entities.Turno;

public interface ITurnoService {

    TurnoResponseDTO crearTurno(TurnoRequestDTO dto, String username) throws NegocioException;

    List<String> getHorariosDisponibles(Integer servicioId, LocalDate fecha);

    List<TurnoResponseDTO> traerTodos();

    void cancelarTurno(Integer turnoId, String codigoAnulacion) throws NegocioException;

    void cancelarTurnoPorCodigo(String codigoAnulacion) throws NegocioException;

    List<TurnoResponseDTO> obtenerTurnosPorCliente(Integer clienteId);

    List<Turno> findAll();

    DisponibilidadResponseDTO obtenerDisponibilidadPorDiaYServicio(LocalDate fecha, Integer servicioId);

    List<LocalDate> obtenerDiasDisponiblesParaServicio(Servicio servicio);

    List<String> obtenerFechasHabilitadasPorMes(Servicio servicio, int year, int month);

    List<TurnoResponseDTO> traerSoloTurnosReservados();

    List<TurnoAdminDTO> obtenerTurnosNoDisponibles();

}
