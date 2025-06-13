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

    boolean existsByEmpleadoAndFechaHora(Empleado empleado, LocalDateTime fechaHora);

    @Query("SELECT t FROM Turno t WHERE t.servicio.id = :servicioId AND t.fechaHora >= :inicio AND t.fechaHora < :fin")
    List<Turno> findByServicioIdAndFecha(@Param("servicioId") Integer servicioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    Optional<Turno> findByCodigoAnulacion(String codigo);

}
