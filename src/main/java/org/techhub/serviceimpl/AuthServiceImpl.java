package org.techhub.serviceimpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.techhub.dto.LoginRequest;
import org.techhub.dto.LoginResponse;
import org.techhub.dto.RegisterRequest;
import org.techhub.entity.Role;
import org.techhub.entity.User;
import org.techhub.entity.UserRole;
import org.techhub.exception.UserNotFoundException;
import org.techhub.repository.RoleRepository;
import org.techhub.repository.UserRepository;
import org.techhub.repository.UserRoleRepository;
import org.techhub.security.JwtService;
import org.techhub.service.AuthService;
import org.techhub.service.UserSessionService;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSessionService userSessionService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserSessionService userSessionService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userSessionService = userSessionService;
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @Override
    @Transactional
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
                        new UserNotFoundException("User not found with email: " + email));

        String token = jwtService.generateToken(user.getEmail());

        // Save session in database for stateful validation & logout tracking
        userSessionService.saveSession(user, token);

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
    @Transactional
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

        // Verify role is not null (defensive programming)
        if (userRole == null) {
            throw new RuntimeException(
                    "Failed to retrieve USER role - unexpected null value");
        }

        try {
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

            if (savedUser == null) {
                throw new RuntimeException(
                        "Failed to save user - unexpected null value");
            }

            // Create UserRole
            UserRole userRoleEntity = new UserRole();

            userRoleEntity.setUser(savedUser);

            userRoleEntity.setRole(userRole);

            UserRole savedUserRole = userRoleRepository.save(userRoleEntity);

            if (savedUserRole == null) {
                throw new RuntimeException(
                        "Failed to assign role to user");
            }

            return "User registration successful";

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error during user registration: " + e.getMessage(), e);
        }
    }
}