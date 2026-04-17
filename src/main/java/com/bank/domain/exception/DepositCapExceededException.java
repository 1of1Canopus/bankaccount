package com.bank.domain.exception;

import java.math.BigDecimal;

public class DepositCapExceededException extends RuntimeException {

    public DepositCapExceededException(BigDecimal depositCap) {
        super("Deposit exceeds the account cap of " + depositCap);
    }
}
