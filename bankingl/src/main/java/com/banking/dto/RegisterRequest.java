package com.banking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Full name required")
    private String fullName;

    @Email(message = "Valid email required")
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "CNIC required")
    @Pattern(regexp = "\\d{5}-\\d{7}-\\d{1}", message = "CNIC format: 12345-1234567-1")
    private String cnic;

    @NotBlank
    @Pattern(regexp = "\\+92\\d{10}", message = "Phone: +923001234567")
    private String phoneNumber;
}
