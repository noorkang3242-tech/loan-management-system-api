package com.banking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(nullable = false, updatable = false)
    private LocalDateTime paidAt;

    private String transactionReference;

    @PrePersist
    public void prePersist() {
        this.paidAt = LocalDateTime.now();
        this.transactionReference = "TXN-" + System.currentTimeMillis();
    }
}
