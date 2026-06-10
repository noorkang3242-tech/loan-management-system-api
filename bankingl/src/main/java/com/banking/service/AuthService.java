package com.banking.service;

import com.banking.dto.AuthResponse;
import com.banking.dto.LoginRequest;
import com.banking.dto.RegisterRequest;
import com.banking.entity.User;
import com.banking.enums.Role;
import com.banking.exception.DuplicateResourceException;
import com.banking.repository.UserRepository;
import com.banking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Duplicate check
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByCnic(request.getCnic())) {
            throw new DuplicateResourceException("CNIC already registered: " + request.getCnic());
        }

        // User banana
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .cnic(request.getCnic())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ROLE_CUSTOMER) // Default role customer
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message("Registration successful! Welcome " + user.getFullName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Spring Security se authenticate karna
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message("Login successful! Welcome back " + user.getFullName())
                .build();
    }

    // Admin user banana (testing ke liye)
    public AuthResponse registerAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User admin = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .cnic(request.getCnic())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .build();

        userRepository.save(admin);
        String token = jwtService.generateToken(admin);

        return AuthResponse.builder()
                .token(token)
                .email(admin.getEmail())
                .fullName(admin.getFullName())
                .role(admin.getRole())
                .message("Admin registered successfully")
                .build();
    }
}
