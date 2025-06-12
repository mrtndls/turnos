package com.unla.grupo16.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unla.grupo16.models.entities.Disponibilidad;
import org.springframework.stereotype.Repository;

@Repository
public interface IDisponibilidadRepository  extends JpaRepository<Disponibilidad, Integer> {
    // Aquí puedes agregar métodos personalizados si es necesario
  
}
