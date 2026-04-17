package com.bank.application;

import com.bank.application.service.AccountService;
import com.bank.domain.model.AccountAudit;
import com.bank.domain.model.AccountType;
import com.bank.domain.model.OperationType;
import com.bank.domain.model.SavingsAccount;
import com.bank.domain.repository.AccountRepository;
import com.bank.infrastructure.persistence.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class AccountAuditServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        AccountRepository repository = new InMemoryAccountRepository();
        accountService = new AccountService(repository);
    }

    @Test
    void auditForCurrentAccountHasCorrectType() {
        var account = accountService.createAccount(BigDecimal.ZERO);

        AccountAudit statement = accountService.getAudit(account.getAccountNumber());

        assertThat(statement.getAccountType()).isEqualTo(AccountType.CURRENT);
    }

    @Test
    void auditForSavingsAccountHasCorrectType() {
        SavingsAccount account = accountService.createSavingsAccount(new BigDecimal("22950.00"));

        AccountAudit statement = accountService.getAudit(account.getAccountNumber());

        assertThat(statement.getAccountType()).isEqualTo(AccountType.SAVINGS);
    }

    @Test
    void auditReflectsCurrentBalance() {
        var account = accountService.createAccount(BigDecimal.ZERO);
        accountService.deposit(account.getAccountNumber(), new BigDecimal("300.00"));
        accountService.withdraw(account.getAccountNumber(), new BigDecimal("100.00"));

        AccountAudit statement = accountService.getAudit(account.getAccountNumber());

        assertThat(statement.getBalance()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    void auditOperationsAreSortedReverseChronologically() {
        var account = accountService.createAccount(BigDecimal.ZERO);
        accountService.deposit(account.getAccountNumber(), new BigDecimal("100.00"));
        accountService.deposit(account.getAccountNumber(), new BigDecimal("200.00"));
        accountService.withdraw(account.getAccountNumber(), new BigDecimal("50.00"));

        AccountAudit statement = accountService.getAudit(account.getAccountNumber());

        assertThat(statement.getOperations()).hasSize(3);
        assertThat(statement.getOperations().get(0).getType()).isEqualTo(OperationType.WITHDRAWAL);
        assertThat(statement.getOperations().get(1).getType()).isEqualTo(OperationType.DEPOSIT);
        assertThat(statement.getOperations().get(2).getType()).isEqualTo(OperationType.DEPOSIT);
    }

    @Test
    void auditOperationsContainCorrectAmounts() {
        var account = accountService.createAccount(BigDecimal.ZERO);
        accountService.deposit(account.getAccountNumber(), new BigDecimal("500.00"));
        accountService.withdraw(account.getAccountNumber(), new BigDecimal("150.00"));

        AccountAudit statement = accountService.getAudit(account.getAccountNumber());

        assertThat(statement.getOperations().get(0).getAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(statement.getOperations().get(1).getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    void auditWithNoOperationsIsEmpty() {
        var account = accountService.createAccount(BigDecimal.ZERO);

        AccountAudit statement = accountService.getAudit(account.getAccountNumber());

        assertThat(statement.getOperations()).isEmpty();
    }
}
