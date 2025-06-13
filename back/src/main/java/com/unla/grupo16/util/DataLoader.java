package com.unla.grupo16.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.unla.grupo16.models.entities.Disponibilidad;
import com.unla.grupo16.models.entities.Empleado;
import com.unla.grupo16.models.entities.Localidad;
import com.unla.grupo16.models.entities.Servicio;
import com.unla.grupo16.models.entities.Ubicacion;
import com.unla.grupo16.repositories.IDisponibilidadRepository;
import com.unla.grupo16.repositories.IEmpleadoRepository;
import com.unla.grupo16.repositories.ILocalidadRepository;
import com.unla.grupo16.repositories.IServicioRepository;
import com.unla.grupo16.repositories.IUbicacionRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(
            IServicioRepository servicioRepo,
            IDisponibilidadRepository disponibilidadRepo,
            IEmpleadoRepository empleadoRepo,
            ILocalidadRepository localidadRepo,
            IUbicacionRepository ubicacionRepo) {

        return args -> {
            // Verificar si ya existe la localidad "LDZ!"
            Localidad localidad = localidadRepo.findByNombre("LDZ!").orElseGet(() -> {
                Localidad newLocalidad = new Localidad();
                newLocalidad.setNombre("LDZ!");
                return localidadRepo.save(newLocalidad);
            });

            // Verificar si ya existe la ubicacion "Calle Falsa 1234"
            Ubicacion ubicacion = ubicacionRepo.findByDireccion("Calle Falsa 1234").orElseGet(() -> {
                Ubicacion newUbicacion = new Ubicacion();
                newUbicacion.setDireccion("Calle Falsa 1234");
                newUbicacion.setLocalidad(localidad);
                return ubicacionRepo.save(newUbicacion);
            });

            // Verificar si ya existe disponibilidad para viernes 9:00-9:45
            Disponibilidad dispViernes = disponibilidadRepo.findByDiaSemanaAndHoraInicioAndHoraFin(
                    DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(9, 45))
                    .orElseGet(() -> {
                        Disponibilidad newDisp = Disponibilidad.builder()
                                .diaSemana(DayOfWeek.FRIDAY)
                                .horaInicio(LocalTime.of(9, 0))
                                .horaFin(LocalTime.of(9, 45))
                                .build();
                        return disponibilidadRepo.save(newDisp);
                    });

            // Verificar si ya existe el servicio "VTV"
            servicioRepo.findByNombre("VTV").orElseGet(() -> {
                Servicio servicio = Servicio.builder()
                        .nombre("VTV")
                        .duracion(45)
                        .disponibilidad(dispViernes)
                        .ubicaciones(Set.of(ubicacion))
                        .build();

                dispViernes.getServicios().add(servicio);
                disponibilidadRepo.save(dispViernes);

                return servicioRepo.save(servicio);
            });

            // Verificar si ya existe empleado con dni "12345678"
            empleadoRepo.findByDni("12345678").orElseGet(() -> {
                Empleado empleado = new Empleado();
                empleado.setNombre("EmpleadoEjemplo");
                empleado.setApellido("ApellidoEjemplo");
                empleado.setDni("12345678");
                empleado.setLegajo("LEG001");
                return empleadoRepo.save(empleado);
            });
        };
    }
}
