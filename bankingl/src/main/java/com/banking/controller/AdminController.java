package com.banking.controller;

import com.banking.dto.*;
import com.banking.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final LoanService loanService;

    /**
     * GET /api/admin/loans
     * Saari loans dekhna
     */
    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getAllLoans() {
        List<LoanResponse> loans = loanService.getAllLoans();
        return ResponseEntity.ok(ApiResponse.success("All loans retrieved", loans));
    }

    /**
     * GET /api/admin/loans/pending
     * Pending approval wali loans
     */
    @GetMapping("/loans/pending")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getPendingLoans() {
        List<LoanResponse> loans = loanService.getPendingLoans();
        return ResponseEntity.ok(ApiResponse.success("Pending loans: " + loans.size(), loans));
    }

    /**
     * PUT /api/admin/loans/{id}/status
     * Loan approve ya reject karna
     */
    @PutMapping("/loans/{id}/status")
    public ResponseEntity<ApiResponse<LoanResponse>> updateLoanStatus(
            @PathVariable Long id,
            @Valid @RequestBody LoanStatusUpdateRequest request) {
        LoanResponse loan = loanService.updateLoanStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Loan status updated to: " + loan.getStatus(), loan));
    }
}
