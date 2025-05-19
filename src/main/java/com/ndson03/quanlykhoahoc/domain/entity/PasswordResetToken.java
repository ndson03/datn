package com.ndson03.quanlykhoahoc.domain.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    // Sử dụng accountType để phân biệt giữa Teacher và Student
    @Column(name = "account_type", nullable = false)
    private String accountType;  // "TEACHER" hoặc "STUDENT"

    @Column(name = "account_id", nullable = false)
    private int accountId;       // ID của Teacher hoặc Student

    @Column(name = "expiry_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, String accountType, int accountId, Date expiryDate) {
        this.token = token;
        this.accountType = accountType;
        this.accountId = accountId;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}