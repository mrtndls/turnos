package com.unla.grupo16.services.impl;

import java.text.MessageFormat;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // faltaba este import

import com.unla.grupo16.models.entities.UserEntity;
import com.unla.grupo16.repositories.IUserRepository;

@Service("userService")
public class UserServiceImp implements UserDetailsService {

    private final IUserRepository userRepository;

    public UserServiceImp(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailWithPersona(email).orElseThrow(
                () -> new UsernameNotFoundException(
                        MessageFormat.format("Usuario con email {0} no encontrado", email)));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActivo(),
                true,
                true,
                true,
                user.getRoleEntities().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getType().name()))
                        .collect(Collectors.toList())
        );
    }

}
