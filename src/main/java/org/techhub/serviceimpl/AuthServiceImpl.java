package org.techhub.serviceimpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.techhub.dto.LoginRequest;
import org.techhub.dto.LoginResponse;
import org.techhub.dto.RegisterRequest;
import org.techhub.entity.Role;
import org.techhub.entity.User;
import org.techhub.entity.UserRole;
import org.techhub.repository.RoleRepository;
import org.techhub.repository.UserRepository;
import org.techhub.repository.UserRoleRepository;
import org.techhub.security.JwtService;
import org.techhub.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(),
                                loginRequest.getPassword()
                        )
                );

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // =====================================================
    // REGISTER
    // =====================================================

    @Override
    public String register(RegisterRequest registerRequest) {

        // Check email already exists
        if (userRepository
                .findByEmail(registerRequest.getEmail())
                .isPresent()) {

            throw new RuntimeException(
                    "Email already registered");
        }

        // Find USER role
        Role userRole = roleRepository
                .findByName("USER")
                .orElseThrow(() ->
                        new RuntimeException(
                                "USER role not found"));

        // Create User
        User user = new User();

        user.setName(registerRequest.getName());

        user.setEmail(registerRequest.getEmail());

        // BCrypt password
        user.setPassword(
                passwordEncoder.encode(
                        registerRequest.getPassword()
                )
        );

        user.setEnabled(true);

        // Save User
        User savedUser = userRepository.save(user);

        // Create UserRole
        UserRole userRoleEntity = new UserRole();

        userRoleEntity.setUser(savedUser);

        userRoleEntity.setRole(userRole);

        userRoleRepository.save(userRoleEntity);

        return "User registration successful";
    }
}