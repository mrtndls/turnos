package com.unla.grupo16.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.UserEntity;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    @Query("select u from UserEntity u join fetch u.persona where u.email = :email")
    Optional<UserEntity> findByEmailConPersona(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.persona WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithPersona(@Param("email") String email);

    Optional<UserEntity> findByPersona(Cliente cliente);

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.persona p WHERE TYPE(p) = Cliente")
    List<UserEntity> findAllClientesConUsuarioIncluyendoBaja();

}
