package com.bank.domain.model;

import com.bank.domain.exception.DepositCapExceededException;

import java.math.BigDecimal;
import java.util.UUID;

public class SavingsAccount extends Account {

    private final BigDecimal depositCap;

    public SavingsAccount(String accountNumber, BigDecimal depositCap) {
        super(accountNumber, BigDecimal.ZERO);
        this.depositCap = depositCap;
    }

    public static SavingsAccount create(BigDecimal depositCap) {
        return new SavingsAccount(UUID.randomUUID().toString(), depositCap);
    }

    @Override
    public void deposit(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0
                && getBalance().add(amount).compareTo(depositCap) > 0) {
            throw new DepositCapExceededException(depositCap);
        }
        super.deposit(amount);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }

    public BigDecimal getDepositCap() {
        return depositCap;
    }
}
