package org.techhub.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.techhub.entity.Role;
import org.techhub.entity.User;
import org.techhub.entity.UserRole;
import org.techhub.repository.RoleRepository;
import org.techhub.repository.UserRepository;
import org.techhub.repository.UserRoleRepository;

@Component
@Order(2)
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(UserRepository userRepository,
                               RoleRepository roleRepository,
                               UserRoleRepository userRoleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        createAdmin();
        createUser();
    }

    private void createAdmin() {

        if (userRepository.findByEmail("admin@booking.com").isPresent()) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        User admin = new User();
        admin.setName("System Admin");
        admin.setEmail("admin@booking.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setEnabled(true);

        User savedAdmin = userRepository.save(admin);

        UserRole userRole = new UserRole();
        userRole.setUser(savedAdmin);
        userRole.setRole(adminRole);

        userRoleRepository.save(userRole);
    }

    private void createUser() {

        if (userRepository.findByEmail("user@booking.com").isPresent()) {
            return;
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = new User();
        user.setName("Booking User");
        user.setEmail("user@booking.com");
        user.setPassword(passwordEncoder.encode("User@123"));
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setUser(savedUser);
        userRoleEntity.setRole(userRole);

        userRoleRepository.save(userRoleEntity);
    }
}