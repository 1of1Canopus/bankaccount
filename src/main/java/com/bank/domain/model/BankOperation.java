package com.bank.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankOperation {

    private final String accountNumber;
    private final OperationType type;
    private final BigDecimal amount;
    private final LocalDateTime date;

    public BankOperation(String accountNumber, OperationType type, BigDecimal amount, LocalDateTime date) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public OperationType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
