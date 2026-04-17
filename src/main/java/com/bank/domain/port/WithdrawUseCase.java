package com.bank.domain.port;

import java.math.BigDecimal;

public interface WithdrawUseCase {

    void withdraw(String accountNumber, BigDecimal amount);
}
