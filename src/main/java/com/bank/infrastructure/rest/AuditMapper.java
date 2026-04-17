package com.bank.infrastructure.rest;

import com.bank.domain.model.AccountAudit;
import com.bank.domain.model.BankOperation;

import java.util.List;

public class AuditMapper {

    private AuditMapper() {
    }

    public static AccountAuditResponse toResponse(AccountAudit audit) {
        AccountAuditResponse response = new AccountAuditResponse();
        response.setAccountType(audit.getAccountType());
        response.setBalance(audit.getBalance());
        response.setIssuedAt(audit.getIssuedAt());
        response.setOperations(toOperationResponses(audit.getOperations()));
        return response;
    }

    private static List<OperationResponse> toOperationResponses(List<BankOperation> operations) {
        return operations.stream().map(op -> {
            OperationResponse response = new OperationResponse();
            response.setType(op.getType());
            response.setAmount(op.getAmount());
            response.setDate(op.getDate());
            return response;
        }).toList();
    }
}
