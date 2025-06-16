package com.unla.grupo16.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
            // Localidad
            Localidad localidad = localidadRepo.findByNombre("LDZ!").orElseGet(() -> {
                Localidad newLocalidad = new Localidad();
                newLocalidad.setNombre("LDZ!");
                return localidadRepo.save(newLocalidad);
            });

            // Ubicación
            Ubicacion ubicacion = ubicacionRepo.findByDireccion("Calle Falsa 1234").orElseGet(() -> {
                Ubicacion newUbicacion = new Ubicacion();
                newUbicacion.setDireccion("Calle Falsa 1234");
                newUbicacion.setLocalidad(localidad);
                return ubicacionRepo.save(newUbicacion);
            });

            // Servicio VTV
            Servicio servicioVTV = servicioRepo.findByNombre("VTV").orElseGet(() -> {
                Servicio nuevoServicio = Servicio.builder()
                        .nombre("VTV")
                        .duracion(45) // duración típica de servicio
                        .ubicaciones(Set.of(ubicacion))
                        .build();
                return servicioRepo.save(nuevoServicio);
            });

            // Crear disponibilidades para lunes a viernes, de 8:00 a 18:00 en bloques de 45 min (igual que duración servicio)
            List<DayOfWeek> diasLaborales = List.of(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY
            );

            for (DayOfWeek dia : diasLaborales) {
                LocalTime horaInicio = LocalTime.of(8, 0);
                LocalTime horaFin = LocalTime.of(18, 0);
                LocalTime hora = horaInicio;

                while (hora.plusMinutes(servicioVTV.getDuracion()).compareTo(horaFin) <= 0) {
                    LocalTime horaIni = hora;
                    LocalTime horaFn = hora.plusMinutes(servicioVTV.getDuracion());

                    // Busca disponibilidad fuera del lambda para evitar usar variables mutables dentro
                    Optional<Disponibilidad> dispOpt = disponibilidadRepo.findByDiaSemanaAndHoraInicioAndHoraFin(
                            dia, horaIni, horaFn);

                    Disponibilidad disp = dispOpt.orElseGet(() -> {
                        Disponibilidad nuevaDisp = Disponibilidad.builder()
                                .diaSemana(dia)
                                .horaInicio(horaIni)
                                .horaFin(horaFn)
                                .build();
                        return disponibilidadRepo.save(nuevaDisp);
                    });

                    if (!disp.getServicios().contains(servicioVTV)) {
                        disp.getServicios().add(servicioVTV);
                        disponibilidadRepo.save(disp);
                    }

                    hora = hora.plusMinutes(servicioVTV.getDuracion());
                }
            }

            // Empleado 
            String dni = "123456789";
            empleadoRepo.findByDni(dni).orElseGet(() -> {
                Empleado empleado = new Empleado();
                empleado.setNombre("Alberto");
                empleado.setApellido("Perez");
                empleado.setDni(dni);
                empleado.setLegajo("LEG001");
                return empleadoRepo.save(empleado);
            });
        };
    }
}
