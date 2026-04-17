package com.bank.domain;

import com.bank.domain.exception.DepositCapExceededException;
import com.bank.domain.exception.InsufficientFundsException;
import com.bank.domain.model.AccountType;
import com.bank.domain.model.SavingsAccount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void newSavingsAccountHasZeroBalance() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void newSavingsAccountHasSavingsType() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));

        assertThat(account.getAccountType()).isEqualTo(AccountType.SAVINGS);
    }

    @Test
    void newSavingsAccountHasNoOverdraft() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));

        assertThat(account.getOverdraftLimit()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void depositWithinCapSucceeds() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));

        account.deposit(new BigDecimal("1000.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void depositUpToExactCapSucceeds() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));

        account.deposit(new BigDecimal("22950.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("22950.00"));
    }

    @Test
    void depositExceedingCapThrowsDepositCapExceeded() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));
        account.deposit(new BigDecimal("20000.00"));

        assertThatThrownBy(() -> account.deposit(new BigDecimal("5000.00")))
            .isInstanceOf(DepositCapExceededException.class);
    }

    @Test
    void depositExceedingCapFromZeroThrowsDepositCapExceeded() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("1000.00"));

        assertThatThrownBy(() -> account.deposit(new BigDecimal("1500.00")))
            .isInstanceOf(DepositCapExceededException.class);
    }

    @Test
    void withdrawalWithinBalanceSucceeds() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));
        account.deposit(new BigDecimal("500.00"));

        account.withdraw(new BigDecimal("200.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    void withdrawalExceedingBalanceThrowsInsufficientFunds() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));
        account.deposit(new BigDecimal("100.00"));

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("500.00")))
            .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void overdraftNotAllowedOnSavingsAccount() {
        SavingsAccount account = SavingsAccount.create(new BigDecimal("22950.00"));

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("100.00")))
            .isInstanceOf(InsufficientFundsException.class);
    }
}
