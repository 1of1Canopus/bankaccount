package com.bank.infrastructure.rest;

import com.bank.domain.model.AccountAudit;
import com.bank.domain.model.CurrentAccount;
import com.bank.domain.port.CreateAccountUseCase;
import com.bank.domain.port.DepositUseCase;
import com.bank.domain.port.GetAccountAuditUseCase;
import com.bank.domain.port.GetAccountUseCase;
import com.bank.domain.port.WithdrawUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final GetAccountAuditUseCase getAccountAuditUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase,
                             DepositUseCase depositUseCase,
                             WithdrawUseCase withdrawUseCase,
                             GetAccountUseCase getAccountUseCase,
                             GetAccountAuditUseCase getAccountAuditUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.getAccountAuditUseCase = getAccountAuditUseCase;
    }

    @PostMapping
    public ResponseEntity<CurrentAccountResponse> createAccount(@RequestBody(required = false) CreateAccountRequest request) {
        if (request == null) {
            request = new CreateAccountRequest();
        }
        CurrentAccount account = (CurrentAccount) createAccountUseCase.createAccount(request.getOverdraftLimit());
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountMapper.toResponse(account));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<CurrentAccountResponse> getAccount(@PathVariable String accountNumber) {
        CurrentAccount account = (CurrentAccount) getAccountUseCase.getAccount(accountNumber);
        return ResponseEntity.ok(AccountMapper.toResponse(account));
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<CurrentAccountResponse> deposit(@PathVariable String accountNumber,
                                                           @RequestBody AmountRequest request) {
        depositUseCase.deposit(accountNumber, request.getAmount());
        CurrentAccount account = (CurrentAccount) getAccountUseCase.getAccount(accountNumber);
        return ResponseEntity.ok(AccountMapper.toResponse(account));
    }

    @PostMapping("/{accountNumber}/withdrawal")
    public ResponseEntity<CurrentAccountResponse> withdraw(@PathVariable String accountNumber,
                                                            @RequestBody AmountRequest request) {
        withdrawUseCase.withdraw(accountNumber, request.getAmount());
        CurrentAccount account = (CurrentAccount) getAccountUseCase.getAccount(accountNumber);
        return ResponseEntity.ok(AccountMapper.toResponse(account));
    }

    @GetMapping("/{accountNumber}/audit")
    public ResponseEntity<AccountAuditResponse> getAudit(@PathVariable String accountNumber) {
        AccountAudit audit = getAccountAuditUseCase.getAudit(accountNumber);
        return ResponseEntity.ok(AuditMapper.toResponse(audit));
    }
}
