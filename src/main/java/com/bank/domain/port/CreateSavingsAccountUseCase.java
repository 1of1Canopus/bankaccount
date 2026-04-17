package com.bank.domain.port;

import com.bank.domain.model.SavingsAccount;

import java.math.BigDecimal;

public interface CreateSavingsAccountUseCase {

    SavingsAccount createSavingsAccount(BigDecimal depositCap);
}
