//package com.finance.api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.finance.api.dto.request.AccountCreateRequestDTO;
//import com.finance.api.repository.AccountRepository;
//import com.finance.api.service.AccountService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AccountController.class)
//class AccountControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockitoBean
//    private AccountRepository accountRepository;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    @MockitoBean
//    private AccountService service;
//
//    @Test
//    @DisplayName("Deve retornar 400 se não informar o ID do usuário")
//    void createInvalidAccount() throws Exception {
//        AccountCreateRequestDTO invalidData = new AccountCreateRequestDTO(
//                "Nubank", BigDecimal.ONE, BigDecimal.ZERO, "9999-1", "BANCO", 1L
//        );
//
//        mvc.perform(post("/account/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidData)))
//                .andExpect(status().isBadRequest());
//    }
//}