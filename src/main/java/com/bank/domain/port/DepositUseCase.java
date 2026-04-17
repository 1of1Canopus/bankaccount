package com.bank.domain.port;

import java.math.BigDecimal;

public interface DepositUseCase {

    void deposit(String accountNumber, BigDecimal amount);
}
