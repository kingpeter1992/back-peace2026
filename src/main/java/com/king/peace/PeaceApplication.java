package com.king.peace;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.king.peace.Dao.RoleRepository;
import com.king.peace.Dao.UserRepository;
import com.king.peace.Entitys.ERole;
import com.king.peace.Entitys.Role;
import com.king.peace.Entitys.User;

@SpringBootApplication
public class PeaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeaceApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder encoder) {

        return args -> {

            System.out.println("🚀 Initialisation des rôles...");

            // ===== CREATION DES ROLES =====
            createRoleIfNotExists(roleRepository, ERole.ROLE_ADMIN);
            createRoleIfNotExists(roleRepository, ERole.ROLE_CAISSIER);
            createRoleIfNotExists(roleRepository, ERole.ROLE_RESPONSABLE_PERSONNEL);
            createRoleIfNotExists(roleRepository, ERole.ROLE_USER);

            System.out.println("🚀 Initialisation utilisateur admin...");

            // ===== CREATION ADMIN =====
            if (userRepository.findByEmail("kingkapeta@gmail.com").isEmpty()) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("kingkapeta@gmail.com");
                admin.setPassword(encoder.encode("123456789"));
                admin.setActive(true);

                Set<Role> roles = new HashSet<>();
                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

                roles.add(adminRole);
                admin.setRoles(roles);

                userRepository.save(admin);

                System.out.println("✅ Utilisateur ADMIN créé !");
            } else {
                System.out.println("ℹ️ Admin déjà existant.");
            }

            System.out.println("🎯 Initialisation terminée.");
        };
    }

    private void createRoleIfNotExists(RoleRepository repository, ERole roleName) {
        if (repository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            repository.save(role);
            System.out.println("✔ Role créé : " + roleName);
        }
    }
}
