package com.bank.domain;

import com.bank.domain.exception.InsufficientFundsException;
import com.bank.domain.model.AccountType;
import com.bank.domain.model.CurrentAccount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class CurrentAccountTest {

    @Test
    void newAccountHasZeroBalance() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void newAccountHasCurrentType() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);

        assertThat(account.getAccountType()).isEqualTo(AccountType.CURRENT);
    }

    @Test
    void newAccountHasUniqueAccountNumber() {
        CurrentAccount first = CurrentAccount.create(BigDecimal.ZERO);
        CurrentAccount second = CurrentAccount.create(BigDecimal.ZERO);

        assertThat(first.getAccountNumber()).isNotBlank();
        assertThat(first.getAccountNumber()).isNotEqualTo(second.getAccountNumber());
    }

    @Test
    void depositIncreasesBalance() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);

        account.deposit(new BigDecimal("100.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void multipleDepositsAccumulate() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);

        account.deposit(new BigDecimal("100.00"));
        account.deposit(new BigDecimal("50.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    void withdrawalDecreasesBalance() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);
        account.deposit(new BigDecimal("100.00"));

        account.withdraw(new BigDecimal("30.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("70.00"));
    }

    @Test
    void withdrawalOfFullBalanceSucceeds() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);
        account.deposit(new BigDecimal("100.00"));

        account.withdraw(new BigDecimal("100.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void withdrawalExceedingBalanceThrowsInsufficientFunds() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);
        account.deposit(new BigDecimal("50.00"));

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("100.00")))
            .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdrawalOnEmptyAccountThrowsInsufficientFunds() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("10.00")))
            .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void depositWithZeroAmountThrowsIllegalArgument() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);

        assertThatThrownBy(() -> account.deposit(BigDecimal.ZERO))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void depositWithNegativeAmountThrowsIllegalArgument() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);

        assertThatThrownBy(() -> account.deposit(new BigDecimal("-10.00")))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withdrawalWithZeroAmountThrowsIllegalArgument() {
        CurrentAccount account = CurrentAccount.create(BigDecimal.ZERO);
        account.deposit(new BigDecimal("100.00"));

        assertThatThrownBy(() -> account.withdraw(BigDecimal.ZERO))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withdrawalWithinOverdraftLimitSucceeds() {
        CurrentAccount account = CurrentAccount.create(new BigDecimal("200.00"));
        account.deposit(new BigDecimal("100.00"));

        account.withdraw(new BigDecimal("250.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("-150.00"));
    }

    @Test
    void withdrawalExceedingOverdraftLimitThrowsInsufficientFunds() {
        CurrentAccount account = CurrentAccount.create(new BigDecimal("200.00"));
        account.deposit(new BigDecimal("100.00"));

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("400.00")))
            .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdrawalUpToExactOverdraftLimitSucceeds() {
        CurrentAccount account = CurrentAccount.create(new BigDecimal("200.00"));

        account.withdraw(new BigDecimal("200.00"));

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("-200.00"));
    }
}
