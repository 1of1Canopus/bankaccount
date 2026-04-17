package com.bank.infrastructure.rest;

import com.bank.domain.model.CurrentAccount;
import com.bank.domain.model.SavingsAccount;

public class AccountMapper {

    private AccountMapper() {
    }

    public static CurrentAccountResponse toResponse(CurrentAccount account) {
        CurrentAccountResponse response = new CurrentAccountResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setOverdraftLimit(account.getOverdraftLimit());
        return response;
    }

    public static SavingsAccountResponse toResponse(SavingsAccount account) {
        SavingsAccountResponse response = new SavingsAccountResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setDepositCap(account.getDepositCap());
        return response;
    }
}
