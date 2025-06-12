package com.unla.grupo16.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.grupo16.models.entities.Turno;

@Repository
public interface ITurnoRepository extends JpaRepository<Turno, Integer> {
    // Aquí puedes agregar métodos personalizados si es necesario
  
}
