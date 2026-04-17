package com.bank.infrastructure.rest;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class CreateAccountRequest {

    @DecimalMin(value = "0.00")
    private BigDecimal overdraftLimit = BigDecimal.ZERO;

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}
