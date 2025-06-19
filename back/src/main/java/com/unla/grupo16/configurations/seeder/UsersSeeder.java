package com.unla.grupo16.configurations.seeder;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.unla.grupo16.models.entities.Cliente;
import com.unla.grupo16.models.entities.RoleEntity;
import com.unla.grupo16.models.entities.UserEntity;
import com.unla.grupo16.models.enums.RoleType;
import com.unla.grupo16.repositories.IClienteRepository;
import com.unla.grupo16.repositories.IRoleRepository;
import com.unla.grupo16.repositories.IUserRepository;

@Component
public class UsersSeeder implements CommandLineRunner {

    private static final String PASSWORD_GENERIC = "pw1234";

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IClienteRepository clienteRepository;

    public UsersSeeder(IUserRepository userRepository,
            IRoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            IClienteRepository clienteRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createRolesIfNotExist();
        createUsersIfNotExist();
    }

    private void createRolesIfNotExist() {
        if (roleRepository.count() == 0) {
            roleRepository.save(buildRole(RoleType.USER));   // Cliente
            roleRepository.save(buildRole(RoleType.ADMIN));  // Admin
        }
    }

    private void createUsersIfNotExist() {
        if (userRepository.count() == 0) {
            createAdminUser("admin@gmail.com", PASSWORD_GENERIC);
            createClientUser("cliente@hotmail.com", PASSWORD_GENERIC);
        }
    }

    private void createAdminUser(String email, String password) {
        UserEntity admin = UserEntity.builder()
                .email(email)
                .activo(true)
                .password(passwordEncoder.encode(password))
                .roleEntities(Set.of(roleRepository.findByType(RoleType.ADMIN).orElseThrow(()
                        -> new RuntimeException("Role ADMIN no encontrado"))))
                .build();
        System.out.println("\n\n**ADMIN**\nusername: " + email + "\npassword:" + password);
        userRepository.save(admin);
    }

    private void createClientUser(String email, String password) {
        Cliente cliente = new Cliente();
        cliente.setNombre("Martin");
        cliente.setApellido("Diale");
        cliente.setDni("4123");
        cliente.setCuil("20-4123-9");

        // Persisto primero el cliente para que tenga ID y sea "managed"
        clienteRepository.save(cliente);

        UserEntity client = UserEntity.builder()
                .email(email)
                .activo(true)
                .password(passwordEncoder.encode(password))
                .roleEntities(Set.of(roleRepository.findByType(RoleType.USER).orElseThrow(()
                        -> new RuntimeException("Role USER no encontrado"))))
                .persona(cliente) // Cliente ya guardado y persistente
                .build();
        System.out.println("\n\n**CLIENTE**\nusername: " + email + "\npassword:" + password + "\n");
        userRepository.save(client);
    }

    private RoleEntity buildRole(RoleType roleType) {
        return RoleEntity.builder()
                .type(roleType)
                .build();
    }

}
