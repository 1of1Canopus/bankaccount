package com.bank.infrastructure.config;

import com.bank.application.service.AccountService;
import com.bank.domain.repository.AccountRepository;
import com.bank.infrastructure.persistence.InMemoryAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public AccountRepository accountRepository() {
        return new InMemoryAccountRepository();
    }

    @Bean
    public AccountService accountService(AccountRepository accountRepository) {
        return new AccountService(accountRepository);
    }
}
