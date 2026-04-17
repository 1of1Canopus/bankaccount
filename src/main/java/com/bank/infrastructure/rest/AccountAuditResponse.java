package com.bank.infrastructure.rest;

import com.bank.domain.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AccountAuditResponse {

    private AccountType accountType;
    private BigDecimal balance;
    private LocalDate issuedAt;
    private List<OperationResponse> operations;

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

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
    }

    public List<OperationResponse> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationResponse> operations) {
        this.operations = operations;
    }
}
