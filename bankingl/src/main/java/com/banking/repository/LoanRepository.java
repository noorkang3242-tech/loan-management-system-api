package com.banking.repository;

import com.banking.entity.Loan;
import com.banking.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId ORDER BY l.appliedAt DESC")
    List<Loan> findAllByUserIdOrderByDate(Long userId);
}
