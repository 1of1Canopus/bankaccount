package com.bank.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AccountAudit {

    private final AccountType accountType;
    private final BigDecimal balance;
    private final LocalDate issuedAt;
    private final List<BankOperation> operations;

    public AccountAudit(AccountType accountType, BigDecimal balance, LocalDate issuedAt, List<BankOperation> operations) {
        this.accountType = accountType;
        this.balance = balance;
        this.issuedAt = issuedAt;
        this.operations = operations;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public List<BankOperation> getOperations() {
        return operations;
    }
}
