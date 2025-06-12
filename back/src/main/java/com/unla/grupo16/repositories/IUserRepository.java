package com.unla.grupo16.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.UserEntity; // Asegurate de importar Cliente

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    @Query("select u from UserEntity u join fetch u.persona where u.email = :email")
    Optional<UserEntity> findByEmailConPersona(@Param("email") String email);

    Optional<UserEntity> findByPersona(Cliente cliente);
}
