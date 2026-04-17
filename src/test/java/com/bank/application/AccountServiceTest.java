package com.bank.application;

import com.bank.application.service.AccountService;
import com.bank.domain.exception.AccountNotFoundException;
import com.bank.domain.exception.DepositCapExceededException;
import com.bank.domain.exception.InsufficientFundsException;
import com.bank.domain.model.Account;
import com.bank.domain.model.SavingsAccount;
import com.bank.domain.repository.AccountRepository;
import com.bank.infrastructure.persistence.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        AccountRepository repository = new InMemoryAccountRepository();
        accountService = new AccountService(repository);
    }

    @Test
    void createAccountReturnsAccountWithUniqueNumber() {
        Account account = accountService.createAccount(BigDecimal.ZERO);

        assertThat(account.getAccountNumber()).isNotBlank();
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createAccountWithOverdraftStoresLimit() {
        Account account = accountService.createAccount(new BigDecimal("500.00"));

        assertThat(account.getOverdraftLimit()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    void depositUpdatesBalance() {
        Account account = accountService.createAccount(BigDecimal.ZERO);

        accountService.deposit(account.getAccountNumber(), new BigDecimal("200.00"));

        Account updated = accountService.getAccount(account.getAccountNumber());
        assertThat(updated.getBalance()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    void withdrawalUpdatesBalance() {
        Account account = accountService.createAccount(BigDecimal.ZERO);
        accountService.deposit(account.getAccountNumber(), new BigDecimal("300.00"));

        accountService.withdraw(account.getAccountNumber(), new BigDecimal("100.00"));

        Account updated = accountService.getAccount(account.getAccountNumber());
        assertThat(updated.getBalance()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    void withdrawalExceedingBalanceThrowsInsufficientFunds() {
        Account account = accountService.createAccount(BigDecimal.ZERO);
        accountService.deposit(account.getAccountNumber(), new BigDecimal("50.00"));

        assertThatThrownBy(() -> accountService.withdraw(account.getAccountNumber(), new BigDecimal("100.00")))
            .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdrawalWithinOverdraftLimitSucceeds() {
        Account account = accountService.createAccount(new BigDecimal("200.00"));
        accountService.deposit(account.getAccountNumber(), new BigDecimal("100.00"));

        accountService.withdraw(account.getAccountNumber(), new BigDecimal("250.00"));

        Account updated = accountService.getAccount(account.getAccountNumber());
        assertThat(updated.getBalance()).isEqualByComparingTo(new BigDecimal("-150.00"));
    }

    @Test
    void withdrawalExceedingOverdraftLimitThrowsInsufficientFunds() {
        Account account = accountService.createAccount(new BigDecimal("200.00"));
        accountService.deposit(account.getAccountNumber(), new BigDecimal("100.00"));

        assertThatThrownBy(() -> accountService.withdraw(account.getAccountNumber(), new BigDecimal("400.00")))
            .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void createSavingsAccountStoresDepositCap() {
        SavingsAccount account = accountService.createSavingsAccount(new BigDecimal("22950.00"));

        assertThat(account.getDepositCap()).isEqualByComparingTo(new BigDecimal("22950.00"));
        assertThat(account.getOverdraftLimit()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void depositOnSavingsAccountExceedingCapThrowsDepositCapExceeded() {
        SavingsAccount account = accountService.createSavingsAccount(new BigDecimal("1000.00"));

        assertThatThrownBy(() -> accountService.deposit(account.getAccountNumber(), new BigDecimal("1500.00")))
            .isInstanceOf(DepositCapExceededException.class);
    }

    @Test
    void operationOnUnknownAccountThrowsAccountNotFound() {
        assertThatThrownBy(() -> accountService.deposit("unknown-account", new BigDecimal("100.00")))
            .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void getUnknownAccountThrowsAccountNotFound() {
        assertThatThrownBy(() -> accountService.getAccount("unknown-account"))
            .isInstanceOf(AccountNotFoundException.class);
    }
}
