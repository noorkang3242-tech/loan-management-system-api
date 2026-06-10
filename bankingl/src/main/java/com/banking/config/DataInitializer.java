package com.banking.config;

import com.banking.entity.User;
import com.banking.enums.Role;
import com.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Admin user banana (agar na ho toh)
        if (!userRepository.existsByEmail("admin@bank.com")) {
            User admin = User.builder()
                    .fullName("Bank Administrator")
                    .email("admin@bank.com")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .cnic("42201-1234567-1")
                    .phoneNumber("+923001234567")
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
        }

        // Test customer banana
        if (!userRepository.existsByEmail("customer@bank.com")) {
            User customer = User.builder()
                    .fullName("Ahmed Ali")
                    .email("customer@bank.com")
                    .password(passwordEncoder.encode("Customer@1234"))
                    .cnic("42201-7654321-2")
                    .phoneNumber("+923009876543")
                    .role(Role.ROLE_CUSTOMER)
                    .enabled(true)
                    .build();
            userRepository.save(customer);
        }

        System.out.println("\n========================================");
        System.out.println("  Test Users Ready:");
        System.out.println("  Admin    -> admin@bank.com / Admin@1234");
        System.out.println("  Customer -> customer@bank.com / Customer@1234");
        System.out.println("========================================\n");
    }
}
