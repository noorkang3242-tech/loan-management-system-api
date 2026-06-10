package com.banking.dto;

import com.banking.enums.LoanType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanApplicationRequest {
    @NotNull(message = "Loan type required")
    private LoanType loanType;

    @NotNull
    @DecimalMin(value = "10000.00", message = "Minimum loan amount is 10,000")
    @DecimalMax(value = "10000000.00", message = "Maximum loan amount is 10,000,000")
    private BigDecimal principalAmount;

    @NotNull
    @Min(value = 3, message = "Minimum tenure is 3 months")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private Integer tenureMonths;

    @NotBlank(message = "Purpose is required")
    @Size(max = 500)
    private String purpose;
}
