package com.bank.domain.port;

import com.bank.domain.model.AccountAudit;

public interface GetAccountAuditUseCase {

    AccountAudit getAudit(String accountNumber);
}
