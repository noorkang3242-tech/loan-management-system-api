package com.banking.controller;

import com.banking.dto.*;
import com.banking.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    /**
     * POST /api/loans/apply
     * Loan apply karna (customer)
     */
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LoanResponse>> applyLoan(
            @Valid @RequestBody LoanApplicationRequest request) {
        LoanResponse loan = loanService.applyForLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Loan application submitted successfully", loan));
    }

    /**
     * GET /api/loans/my-loans
     * Apni saari loans dekhna
     */
    @GetMapping("/my-loans")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getMyLoans() {
        List<LoanResponse> loans = loanService.getMyLoans();
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved", loans));
    }

    /**
     * GET /api/loans/{id}
     * Ek specific loan ki detail
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(@PathVariable Long id) {
        LoanResponse loan = loanService.getLoanById(id);
        return ResponseEntity.ok(ApiResponse.success("Loan found", loan));
    }

    /**
     * POST /api/loans/{id}/pay
     * EMI ya partial payment karna
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<LoanResponse>> makePayment(
            @PathVariable Long id,
            @Valid @RequestBody LoanPaymentRequest request) {
        LoanResponse loan = loanService.makePayment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", loan));
    }
}
