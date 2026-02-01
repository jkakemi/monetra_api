package com.finance.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.api.infra.controller.dto.AccountRequestDTO;
import com.finance.api.infra.controller.dto.CategoryRequestDTO;
import com.finance.api.infra.controller.dto.LoginRequestDTO;
import com.finance.api.infra.controller.dto.UserRequestDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private String authToken;

    @BeforeAll
    void setup() throws Exception {
        UserRequestDTO userRequest = new UserRequestDTO(
                "Transaction User",
                "transaction.user@email.com",
                "555.666.777-88",
                "password123"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "transaction.user@email.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        authToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    @Order(1)
    @DisplayName("Should create account successfully")
    void shouldCreateAccount() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO(
                "Conta Corrente",
                "FISICO",
                "12345-0",
                new BigDecimal("1000.00"),
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/accounts")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName("Should list user accounts")
    void shouldListAccounts() throws Exception {
        mockMvc.perform(get("/accounts")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Conta Corrente"));
    }

    @Test
    @Order(3)
    @DisplayName("Should create category successfully")
    void shouldCreateCategory() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO(
                "Alimentacao",
                "DESPESA"
        );

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("Should list user categories")
    void shouldListCategories() throws Exception {
        mockMvc.perform(get("/categories")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("Should get monthly analysis")
    void shouldGetMonthlyAnalysis() throws Exception {
        mockMvc.perform(get("/analysis/monthly")
                        .param("month", "1")
                        .param("year", "2026")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @DisplayName("Should get daily analysis")
    void shouldGetDailyAnalysis() throws Exception {
        mockMvc.perform(get("/analysis/daily")
                        .param("month", "1")
                        .param("year", "2026")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @DisplayName("Should get total analysis")
    void shouldGetTotalAnalysis() throws Exception {
        mockMvc.perform(get("/analysis/total")
                        .param("month", "1")
                        .param("year", "2026")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }
}
