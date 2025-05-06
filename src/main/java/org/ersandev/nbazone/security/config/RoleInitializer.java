package org.ersandev.nbazone.security.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.ersandev.nbazone.user.AppRole;
import org.ersandev.nbazone.user.Role;
import org.ersandev.nbazone.user.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        System.out.println("🟢 RoleInitializer çalıştı");

        for (AppRole appRole : AppRole.values()) {
            if (!roleRepository.existsByRoleName(appRole)) {
                Role role = new Role();
                role.setRoleName(appRole);
                roleRepository.save(role);
                System.out.println("✅ Rol eklendi: " + appRole);
            }
        }
    }
}