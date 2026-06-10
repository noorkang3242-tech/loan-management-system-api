package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingSystemApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Banking System Started Successfully!");
        System.out.println("  API Base URL : http://localhost:8080/api");
        System.out.println("  H2 Console   : http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }
}
