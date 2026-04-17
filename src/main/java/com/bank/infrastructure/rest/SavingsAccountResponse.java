package com.bank.infrastructure.rest;

import com.bank.domain.model.AccountType;

import java.math.BigDecimal;

public class SavingsAccountResponse {

    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal depositCap;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDepositCap() {
        return depositCap;
    }

    public void setDepositCap(BigDecimal depositCap) {
        this.depositCap = depositCap;
    }
}
