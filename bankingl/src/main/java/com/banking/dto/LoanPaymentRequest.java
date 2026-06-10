package com.banking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanPaymentRequest {
    @NotNull
    @DecimalMin(value = "100.00", message = "Minimum payment is 100")
    private BigDecimal amountPaid;
}
