package com.banking.service;

import com.banking.dto.LoanApplicationRequest;
import com.banking.dto.LoanPaymentRequest;
import com.banking.dto.LoanResponse;
import com.banking.dto.LoanStatusUpdateRequest;
import com.banking.entity.Loan;
import com.banking.entity.LoanPayment;
import com.banking.entity.User;
import com.banking.enums.LoanStatus;
import com.banking.exception.BusinessException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.LoanPaymentRepository;
import com.banking.repository.LoanRepository;
import com.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final UserRepository userRepository;

    // Interest rates per loan type (annual %)
    private static final java.util.Map<com.banking.enums.LoanType, BigDecimal> INTEREST_RATES =
            new java.util.EnumMap<>(com.banking.enums.LoanType.class);

    static {
        INTEREST_RATES.put(com.banking.enums.LoanType.PERSONAL, new BigDecimal("14.0"));
        INTEREST_RATES.put(com.banking.enums.LoanType.HOME, new BigDecimal("10.0"));
        INTEREST_RATES.put(com.banking.enums.LoanType.CAR, new BigDecimal("12.0"));
        INTEREST_RATES.put(com.banking.enums.LoanType.BUSINESS, new BigDecimal("13.5"));
        INTEREST_RATES.put(com.banking.enums.LoanType.EDUCATION, new BigDecimal("8.0"));
    }

    // Loan apply karna
    @Transactional
    public LoanResponse applyForLoan(LoanApplicationRequest request) {
        User currentUser = getCurrentUser();

        // Check: pehle se koi active loan toh nahi
        boolean hasActiveLoan = loanRepository
                .findByUserIdAndStatus(currentUser.getId(), LoanStatus.ACTIVE)
                .size() > 0;

        if (hasActiveLoan) {
            throw new BusinessException("You already have an active loan. Please repay it first.");
        }

        BigDecimal interestRate = INTEREST_RATES.get(request.getLoanType());
        BigDecimal emi = calculateEMI(request.getPrincipalAmount(), interestRate, request.getTenureMonths());
        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(request.getTenureMonths()))
                .setScale(2, RoundingMode.HALF_UP);

        Loan loan = Loan.builder()
                .user(currentUser)
                .loanType(request.getLoanType())
                .principalAmount(request.getPrincipalAmount())
                .interestRate(interestRate)
                .tenureMonths(request.getTenureMonths())
                .monthlyInstallment(emi)
                .totalPayableAmount(totalPayable)
                .remainingAmount(totalPayable)
                .purpose(request.getPurpose())
                .status(LoanStatus.PENDING)
                .build();

        Loan saved = loanRepository.save(loan);
        return mapToResponse(saved);
    }

    // EMI Formula: P * r * (1+r)^n / ((1+r)^n - 1)
    // P = principal, r = monthly rate, n = months
    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, int months) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowerN = onePlusR.pow(months, new MathContext(10));
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    // Customer ki apni loans dekhna
    public List<LoanResponse> getMyLoans() {
        User currentUser = getCurrentUser();
        return loanRepository.findAllByUserIdOrderByDate(currentUser.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Single loan detail
    public LoanResponse getLoanById(Long loanId) {
        Loan loan = findLoanById(loanId);
        ensureOwnerOrAdmin(loan);
        return mapToResponse(loan);
    }

    // Admin: sab loans dekhna
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Admin: pending loans
    public List<LoanResponse> getPendingLoans() {
        return loanRepository.findByStatus(LoanStatus.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Admin: loan approve/reject karna
    @Transactional
    public LoanResponse updateLoanStatus(Long loanId, LoanStatusUpdateRequest request) {
        Loan loan = findLoanById(loanId);

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BusinessException("Only PENDING loans can be approved or rejected");
        }

        loan.setStatus(request.getStatus());
        loan.setAdminRemarks(request.getAdminRemarks());

        if (request.getStatus() == LoanStatus.APPROVED) {
            loan.setApprovedAt(LocalDateTime.now());
            loan.setStatus(LoanStatus.ACTIVE); // Approve hone par active ho jata hai
        }

        return mapToResponse(loanRepository.save(loan));
    }

    // EMI payment karna
    @Transactional
    public LoanResponse makePayment(Long loanId, LoanPaymentRequest request) {
        User currentUser = getCurrentUser();
        Loan loan = findLoanById(loanId);

        if (!loan.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("You can only pay your own loan");
        }

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessException("Payments only allowed for ACTIVE loans");
        }

        if (request.getAmountPaid().compareTo(loan.getRemainingAmount()) > 0) {
            throw new BusinessException("Payment amount exceeds remaining balance: " + loan.getRemainingAmount());
        }

        // Payment record karna
        LoanPayment payment = LoanPayment.builder()
                .loan(loan)
                .amountPaid(request.getAmountPaid())
                .build();
        loanPaymentRepository.save(payment);

        // Remaining amount update
        BigDecimal newRemaining = loan.getRemainingAmount()
                .subtract(request.getAmountPaid())
                .setScale(2, RoundingMode.HALF_UP);
        loan.setRemainingAmount(newRemaining);

        // Loan complete? Close kar do
        if (newRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }

        return mapToResponse(loanRepository.save(loan));
    }

    // Helper methods
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Loan findLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
    }

    private void ensureOwnerOrAdmin(Loan loan) {
        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRole().name().equals("ROLE_ADMIN");
        boolean isOwner = loan.getUser().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new BusinessException("Access denied to this loan");
        }
    }

    private LoanResponse mapToResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .customerName(loan.getUser().getFullName())
                .customerEmail(loan.getUser().getEmail())
                .loanType(loan.getLoanType())
                .principalAmount(loan.getPrincipalAmount())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .monthlyInstallment(loan.getMonthlyInstallment())
                .totalPayableAmount(loan.getTotalPayableAmount())
                .remainingAmount(loan.getRemainingAmount())
                .status(loan.getStatus())
                .purpose(loan.getPurpose())
                .appliedAt(loan.getAppliedAt())
                .approvedAt(loan.getApprovedAt())
                .adminRemarks(loan.getAdminRemarks())
                .build();
    }
}
