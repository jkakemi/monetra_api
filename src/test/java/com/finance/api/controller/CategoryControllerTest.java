//package com.finance.api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.finance.api.dto.request.CategoryCreateRequestDTO;
//import com.finance.api.repository.CategoryRepository;
//import com.finance.api.service.CategoryService;
//import org.junit.jupiter.api.DisplayName;
//import org.springframework.http.MediaType;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//@WebMvcTest(CategoryController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class CategoryControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @MockitoBean
//    private CategoryRepository categoryRepository;
//
//    @MockitoBean
//    private CategoryService categoryService;
//
//   @Test
//   @DisplayName("Deve retornar 400 se nome da categoria for em branco")
//   void createInvalidCategory() throws Exception {
//       CategoryCreateRequestDTO dto =  new CategoryCreateRequestDTO("", "DESPESA", 1L);
//
//       mockMvc.perform(post("/category/create")
//                       .contentType(MediaType.APPLICATION_JSON)
//                       .content(objectMapper.writeValueAsString(dto)))
//               .andExpect(status().isBadRequest());
//
//   }
//
//}