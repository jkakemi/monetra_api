package com.finance.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.api.infra.controller.dto.LoginRequestDTO;
import com.finance.api.infra.controller.dto.UserRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private static String authToken;

    @Test
    @Order(1)
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUser() throws Exception {
        UserRequestDTO request = new UserRequestDTO(
                "Test User",
                "testuser@email.com",
                "123.456.789-01",
                "password123"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName("Should reject duplicate email registration")
    void shouldRejectDuplicateEmail() throws Exception {
        UserRequestDTO request = new UserRequestDTO(
                "Another User",
                "testuser@email.com",
                "987.654.321-00",
                "password456"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("Should login successfully and return JWT token")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO(
                "testuser@email.com",
                "password123"
        );

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    @Order(4)
    @DisplayName("Should reject login with wrong password")
    void shouldRejectWrongPassword() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO(
                "testuser@email.com",
                "wrongpassword"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(5)
    @DisplayName("Should access protected endpoint with valid token")
    void shouldAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@email.com"));
    }

    @Test
    @Order(6)
    @DisplayName("Should reject access without token")
    void shouldRejectAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }
}
