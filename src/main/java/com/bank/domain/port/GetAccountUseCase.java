package com.bank.domain.port;

import com.bank.domain.model.Account;

public interface GetAccountUseCase {

    Account getAccount(String accountNumber);
}
