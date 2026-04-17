package com.bank.infrastructure.rest;

import com.bank.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OperationResponse {

    private OperationType type;
    private BigDecimal amount;
    private LocalDateTime date;

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
