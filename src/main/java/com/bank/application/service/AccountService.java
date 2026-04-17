package com.bank.application.service;

import com.bank.domain.exception.AccountNotFoundException;
import com.bank.domain.model.Account;
import com.bank.domain.model.AccountAudit;
import com.bank.domain.model.BankOperation;
import com.bank.domain.model.CurrentAccount;
import com.bank.domain.model.SavingsAccount;
import com.bank.domain.port.CreateAccountUseCase;
import com.bank.domain.port.CreateSavingsAccountUseCase;
import com.bank.domain.port.DepositUseCase;
import com.bank.domain.port.GetAccountAuditUseCase;
import com.bank.domain.port.GetAccountUseCase;
import com.bank.domain.port.WithdrawUseCase;
import com.bank.domain.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class AccountService implements CreateAccountUseCase, CreateSavingsAccountUseCase, DepositUseCase, WithdrawUseCase, GetAccountUseCase, GetAccountAuditUseCase {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(BigDecimal overdraftLimit) {
        CurrentAccount account = CurrentAccount.create(overdraftLimit);
        return accountRepository.save(account);
    }

    @Override
    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = findAccount(accountNumber);
        account.deposit(amount);
        accountRepository.save(account);
    }

    @Override
    public void withdraw(String accountNumber, BigDecimal amount) {
        Account account = findAccount(accountNumber);
        account.withdraw(amount);
        accountRepository.save(account);
    }

    @Override
    public Account getAccount(String accountNumber) {
        return findAccount(accountNumber);
    }

    @Override
    public SavingsAccount createSavingsAccount(BigDecimal depositCap) {
        SavingsAccount account = SavingsAccount.create(depositCap);
        return (SavingsAccount) accountRepository.save(account);
    }

    @Override
    public AccountAudit getAudit(String accountNumber) {
        Account account = findAccount(accountNumber);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<BankOperation> operations = account.getOperations().stream()
            .filter(op -> op.getDate().isAfter(oneMonthAgo))
            .sorted(Comparator.comparing(BankOperation::getDate).reversed())
            .toList();
        return new AccountAudit(account.getAccountType(), account.getBalance(), LocalDate.now(), operations);
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
