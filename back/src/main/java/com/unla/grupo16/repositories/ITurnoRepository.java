package com.unla.grupo16.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.grupo16.models.entities.Empleado;
import com.unla.grupo16.models.entities.Turno;

@Repository
public interface ITurnoRepository extends JpaRepository<Turno, Integer> {

    // Verifica si un empleado ya tiene un turno en esa fecha y hora
    boolean existsByEmpleadoAndFechaHora(Empleado empleado, LocalDateTime fechaHora);

    // Obtiene turnos para un servicio en un rango de fecha/hora (necesita filtrar por disponible = false)
    @Query("SELECT t FROM Turno t WHERE t.servicio.id = :servicioId AND t.fechaHora >= :inicio AND t.fechaHora < :fin AND t.disponible = false")
    List<Turno> findByServicioIdAndFecha(
            @Param("servicioId") Integer servicioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Busca por código de anulación
    Optional<Turno> findByCodigoAnulacion(String codigo);

    // Todos los turnos del cliente ordenados
    List<Turno> findByClienteIdOrderByFechaHoraDesc(Integer clienteId);

    // ✅ Turnos reservados (no disponibles) de un cliente
    List<Turno> findByClienteIdAndDisponibleFalse(Integer clienteId);

    // ✅ Turnos ocupados en cierto rango para un servicio
    List<Turno> findByServicioIdAndFechaHoraBetweenAndDisponibleFalse(
            Integer servicioId, LocalDateTime inicio, LocalDateTime fin
    );

    boolean existsByEmpleadoAndFechaHoraAndDisponibleFalse(Empleado empleado, LocalDateTime fechaHora);

    List<Turno> findByDisponibleFalseOrderByFechaHoraAsc();

    boolean existsByCliente_IdAndDisponibleFalse(Integer clienteId);

    List<Turno> findByDisponibleFalse();

}
