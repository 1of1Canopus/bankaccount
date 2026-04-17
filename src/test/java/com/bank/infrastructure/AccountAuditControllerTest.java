package com.bank.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountAuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void currentAccountAuditReturnsCorrectType() throws Exception {
        String accountNumber = createCurrentAccount();

        mockMvc.perform(get("/accounts/{number}/audit", accountNumber))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountType").value("CURRENT"))
            .andExpect(jsonPath("$.issuedAt").isNotEmpty())
            .andExpect(jsonPath("$.operations").isArray());
    }

    @Test
    void savingsAccountAuditReturnsCorrectType() throws Exception {
        String accountNumber = createSavingsAccount();

        mockMvc.perform(get("/savings-accounts/{number}/audit", accountNumber))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountType").value("SAVINGS"))
            .andExpect(jsonPath("$.issuedAt").isNotEmpty())
            .andExpect(jsonPath("$.operations").isArray());
    }

    @Test
    void auditShowsOperationsInReverseChronologicalOrder() throws Exception {
        String accountNumber = createCurrentAccount();
        deposit("/accounts", accountNumber, new BigDecimal("100.00"));
        deposit("/accounts", accountNumber, new BigDecimal("200.00"));
        withdraw("/accounts", accountNumber, new BigDecimal("50.00"));

        mockMvc.perform(get("/accounts/{number}/audit", accountNumber))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.operations[0].type").value("WITHDRAWAL"))
            .andExpect(jsonPath("$.operations[0].amount").value(50.0))
            .andExpect(jsonPath("$.operations[1].type").value("DEPOSIT"))
            .andExpect(jsonPath("$.operations[2].type").value("DEPOSIT"));
    }

    @Test
    void auditForUnknownAccountReturns404() throws Exception {
        mockMvc.perform(get("/accounts/nonexistent/audit"))
            .andExpect(status().isNotFound());
    }

    @Test
    void auditWithNoOperationsReturnsEmptyList() throws Exception {
        String accountNumber = createCurrentAccount();

        mockMvc.perform(get("/accounts/{number}/audit", accountNumber))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.operations").isEmpty());
    }

    private String createCurrentAccount() throws Exception {
        var result = mockMvc.perform(post("/accounts"))
            .andExpect(status().isCreated())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
            .get("accountNumber").asText();
    }

    private String createSavingsAccount() throws Exception {
        var result = mockMvc.perform(post("/savings-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("depositCap", 22950.00))))
            .andExpect(status().isCreated())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
            .get("accountNumber").asText();
    }

    private void deposit(String basePath, String accountNumber, BigDecimal amount) throws Exception {
        mockMvc.perform(post(basePath + "/{number}/deposit", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", amount))))
            .andExpect(status().isOk());
    }

    private void withdraw(String basePath, String accountNumber, BigDecimal amount) throws Exception {
        mockMvc.perform(post(basePath + "/{number}/withdrawal", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", amount))))
            .andExpect(status().isOk());
    }
}
