package com.banking.controller;

import com.banking.dto.ApiResponse;
import com.banking.dto.AuthResponse;
import com.banking.dto.LoginRequest;
import com.banking.dto.RegisterRequest;
import com.banking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Naya customer register karna
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    /**
     * POST /api/auth/login
     * Login aur JWT token lena
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * POST /api/auth/register-admin
     * Admin user banana (sirf development mein use karo)
     */
    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin registered", response));
    }
}
