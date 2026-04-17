package com.bank.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class CurrentAccount extends Account {

    public CurrentAccount(String accountNumber, BigDecimal overdraftLimit) {
        super(accountNumber, overdraftLimit);
    }

    public static CurrentAccount create(BigDecimal overdraftLimit) {
        return new CurrentAccount(UUID.randomUUID().toString(), overdraftLimit);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CURRENT;
    }
}
