package com.banking.dto;

import com.banking.enums.LoanStatus;
import com.banking.enums.LoanType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class LoanResponse {
    private Long id;
    private String customerName;
    private String customerEmail;
    private LoanType loanType;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal monthlyInstallment;
    private BigDecimal totalPayableAmount;
    private BigDecimal remainingAmount;
    private LoanStatus status;
    private String purpose;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private String adminRemarks;
}
