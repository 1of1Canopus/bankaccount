package com.bank.infrastructure.rest;

import com.bank.domain.model.AccountAudit;
import com.bank.domain.model.SavingsAccount;
import com.bank.domain.port.CreateSavingsAccountUseCase;
import com.bank.domain.port.DepositUseCase;
import com.bank.domain.port.GetAccountAuditUseCase;
import com.bank.domain.port.GetAccountUseCase;
import com.bank.domain.port.WithdrawUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/savings-accounts")
public class SavingsAccountController {

    private final CreateSavingsAccountUseCase createSavingsAccountUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final GetAccountAuditUseCase getAccountAuditUseCase;

    public SavingsAccountController(CreateSavingsAccountUseCase createSavingsAccountUseCase,
                                     DepositUseCase depositUseCase,
                                     WithdrawUseCase withdrawUseCase,
                                     GetAccountUseCase getAccountUseCase,
                                     GetAccountAuditUseCase getAccountAuditUseCase) {
        this.createSavingsAccountUseCase = createSavingsAccountUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.getAccountAuditUseCase = getAccountAuditUseCase;
    }

    @PostMapping
    public ResponseEntity<SavingsAccountResponse> createSavingsAccount(@RequestBody CreateSavingsAccountRequest request) {
        SavingsAccount account = createSavingsAccountUseCase.createSavingsAccount(request.getDepositCap());
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountMapper.toResponse(account));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<SavingsAccountResponse> getAccount(@PathVariable String accountNumber) {
        SavingsAccount account = (SavingsAccount) getAccountUseCase.getAccount(accountNumber);
        return ResponseEntity.ok(AccountMapper.toResponse(account));
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<SavingsAccountResponse> deposit(@PathVariable String accountNumber,
                                                           @RequestBody AmountRequest request) {
        depositUseCase.deposit(accountNumber, request.getAmount());
        SavingsAccount account = (SavingsAccount) getAccountUseCase.getAccount(accountNumber);
        return ResponseEntity.ok(AccountMapper.toResponse(account));
    }

    @PostMapping("/{accountNumber}/withdrawal")
    public ResponseEntity<SavingsAccountResponse> withdraw(@PathVariable String accountNumber,
                                                            @RequestBody AmountRequest request) {
        withdrawUseCase.withdraw(accountNumber, request.getAmount());
        SavingsAccount account = (SavingsAccount) getAccountUseCase.getAccount(accountNumber);
        return ResponseEntity.ok(AccountMapper.toResponse(account));
    }

    @GetMapping("/{accountNumber}/audit")
    public ResponseEntity<AccountAuditResponse> getAudit(@PathVariable String accountNumber) {
        AccountAudit audit = getAccountAuditUseCase.getAudit(accountNumber);
        return ResponseEntity.ok(AuditMapper.toResponse(audit));
    }
}
