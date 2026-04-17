package com.bank.domain.port;

import com.bank.domain.model.Account;

import java.math.BigDecimal;

public interface CreateAccountUseCase {

    Account createAccount(BigDecimal overdraftLimit);
}
