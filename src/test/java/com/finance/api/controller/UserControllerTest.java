//package com.finance.api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.finance.api.dto.request.UserRegisterRequestDTO;
//import com.finance.api.infra.controller.UserController;
//import com.finance.api.repository.UserRepository;
//import com.finance.api.service.UserService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private UserRepository userRepository;
//
//    @MockitoBean
//    private UserService userService;
//
//    private final ObjectMapper objectMapper =  new ObjectMapper();
//
//    @Test
//    @DisplayName("Deve retornar 400 Bad Request quando dados forem inválidos")
//    void createInvalidUser() throws Exception {
//        UserRegisterRequestDTO invalidData = new UserRegisterRequestDTO(
//                "Maria",
//                "mariaexamplecom",
//                "123"
//        );
//
//        String jsonBody = objectMapper.writeValueAsString(invalidData);
//
//        mockMvc.perform(post("/users/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 201 Created quando dados forem válidos")
//    void createUserSuccess() throws Exception {
//        UserRegisterRequestDTO validData = new UserRegisterRequestDTO(
//                "Maria",
//                "maria@example.com",
//                "123456"
//        );
//        String jsonBody = objectMapper.writeValueAsString(validData);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isCreated());
//    }
//
//}