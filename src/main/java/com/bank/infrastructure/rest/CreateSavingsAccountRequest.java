package com.bank.infrastructure.rest;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateSavingsAccountRequest {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal depositCap;

    public BigDecimal getDepositCap() {
        return depositCap;
    }

    public void setDepositCap(BigDecimal depositCap) {
        this.depositCap = depositCap;
    }
}
