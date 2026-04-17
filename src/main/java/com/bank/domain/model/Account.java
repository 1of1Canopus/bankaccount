package com.bank.domain.model;

import com.bank.domain.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Account {

    private final String accountNumber;
    private BigDecimal balance;
    private final BigDecimal overdraftLimit;
    private final List<BankOperation> operations = new ArrayList<>();

    protected Account(String accountNumber, BigDecimal overdraftLimit) {
        this.accountNumber = accountNumber;
        this.balance = BigDecimal.ZERO;
        this.overdraftLimit = overdraftLimit;
    }

    public abstract AccountType getAccountType();

    public void deposit(BigDecimal amount) {
        validatePositiveAmount(amount);
        balance = balance.add(amount);
        operations.add(new BankOperation(accountNumber, OperationType.DEPOSIT, amount, LocalDateTime.now()));
    }

    public void withdraw(BigDecimal amount) {
        validatePositiveAmount(amount);
        if (amount.compareTo(balance.add(overdraftLimit)) > 0) {
            throw new InsufficientFundsException(
                "Withdrawal of " + amount + " exceeds available balance and overdraft limit"
            );
        }
        balance = balance.subtract(amount);
        operations.add(new BankOperation(accountNumber, OperationType.WITHDRAWAL, amount, LocalDateTime.now()));
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public List<BankOperation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    protected void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
