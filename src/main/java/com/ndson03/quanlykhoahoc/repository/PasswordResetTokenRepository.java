package com.ndson03.quanlykhoahoc.repository;

import com.ndson03.quanlykhoahoc.domain.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByAccountTypeAndAccountId(String accountType, int accountId);
}