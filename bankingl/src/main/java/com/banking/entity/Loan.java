package com.banking.entity;

import com.banking.enums.LoanStatus;
import com.banking.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount; // Total loan amount

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate; // Annual interest rate (%)

    @Column(nullable = false)
    private Integer tenureMonths; // Loan duration in months

    @Column(precision = 15, scale = 2)
    private BigDecimal monthlyInstallment; // EMI amount

    @Column(precision = 15, scale = 2)
    private BigDecimal totalPayableAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    private String purpose; // Why the loan is needed

    @Column(updatable = false)
    private LocalDateTime appliedAt;

    private LocalDateTime approvedAt;

    private String adminRemarks;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanPayment> payments;

    @PrePersist
    public void prePersist() {
        this.appliedAt = LocalDateTime.now();
        this.status = LoanStatus.PENDING;
    }
}
