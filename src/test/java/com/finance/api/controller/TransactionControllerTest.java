//package com.finance.api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.finance.api.dto.request.TransactionCreateRequestDTO;
//import com.finance.api.repository.TransactionRepository;
//import com.finance.api.service.TransactionService;
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
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(TransactionController.class)
//class TransactionControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//    @MockitoBean
//    private TransactionRepository transactionRepository;
//
//    private final ObjectMapper objectMapper  = new ObjectMapper();
//    @MockitoBean private TransactionService service;
//
//    @Test
//    @DisplayName("Deve retornar 400 se o valor for negativo")
//    void createInvalidTransaction() throws Exception {
//        TransactionCreateRequestDTO invalidData = new TransactionCreateRequestDTO(
//                "Gasto",
//                new BigDecimal("-50.00"),
//                "BRL", "2026-01-24", 1L, 1L, 1L
//        );
//
//        mvc.perform(post("/transactions/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidData)))
//                .andExpect(status().isBadRequest());
//    }
//
//}