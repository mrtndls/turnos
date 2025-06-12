package com.unla.grupo16.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.grupo16.models.entities.Ubicacion;

@Repository
public interface IUbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    // Aquí puedes agregar métodos personalizados si es necesario
  
}
