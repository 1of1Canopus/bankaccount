package com.bank.infrastructure;

import com.bank.infrastructure.rest.CurrentAccountResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccountWithoutBodyReturns201WithZeroOverdraft() throws Exception {
        mockMvc.perform(post("/accounts"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").isNotEmpty())
            .andExpect(jsonPath("$.balance").value(0))
            .andExpect(jsonPath("$.overdraftLimit").value(0));
    }

    @Test
    void createAccountWithOverdraftReturns201() throws Exception {
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("overdraftLimit", 200.00))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.overdraftLimit").value(200.0));
    }

    @Test
    void depositReturnsUpdatedBalance() throws Exception {
        String accountNumber = createAccount(BigDecimal.ZERO);

        mockMvc.perform(post("/accounts/{number}/deposit", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 150.00))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(150.0));
    }

    @Test
    void withdrawalReturnsUpdatedBalance() throws Exception {
        String accountNumber = createAccount(BigDecimal.ZERO);
        deposit(accountNumber, new BigDecimal("200.00"));

        mockMvc.perform(post("/accounts/{number}/withdrawal", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 50.00))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(150.0));
    }

    @Test
    void withdrawalExceedingBalanceReturns422() throws Exception {
        String accountNumber = createAccount(BigDecimal.ZERO);
        deposit(accountNumber, new BigDecimal("30.00"));

        mockMvc.perform(post("/accounts/{number}/withdrawal", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 100.00))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void withdrawalWithinOverdraftLimitReturnsNegativeBalance() throws Exception {
        String accountNumber = createAccount(new BigDecimal("200.00"));
        deposit(accountNumber, new BigDecimal("100.00"));

        mockMvc.perform(post("/accounts/{number}/withdrawal", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 250.00))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(-150.0));
    }

    @Test
    void withdrawalExceedingOverdraftLimitReturns422() throws Exception {
        String accountNumber = createAccount(new BigDecimal("200.00"));
        deposit(accountNumber, new BigDecimal("100.00"));

        mockMvc.perform(post("/accounts/{number}/withdrawal", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 400.00))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void getUnknownAccountReturns404() throws Exception {
        mockMvc.perform(get("/accounts/nonexistent"))
            .andExpect(status().isNotFound());
    }

    private String createAccount(BigDecimal overdraftLimit) throws Exception {
        MvcResult result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("overdraftLimit", overdraftLimit))))
            .andExpect(status().isCreated())
            .andReturn();
        CurrentAccountResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), CurrentAccountResponse.class);
        return response.getAccountNumber();
    }

    private void deposit(String accountNumber, BigDecimal amount) throws Exception {
        mockMvc.perform(post("/accounts/{number}/deposit", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", amount))))
            .andExpect(status().isOk());
    }
}
