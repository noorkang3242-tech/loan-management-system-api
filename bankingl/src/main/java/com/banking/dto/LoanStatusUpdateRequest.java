package com.banking.dto;

import com.banking.enums.LoanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanStatusUpdateRequest {
    @NotNull
    private LoanStatus status;
    private String adminRemarks;
}
