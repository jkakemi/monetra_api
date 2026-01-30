//package com.finance.api.service;
//
//import com.finance.api.dto.request.CategoryCreateRequestDTO;
//import com.finance.api.infra.exception.EntityNotFoundException;
//import com.finance.api.model.Category;
//import com.finance.api.model.User;
//import com.finance.api.repository.CategoryRepository;
//import com.finance.api.repository.UserRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.times;
//
//@ExtendWith(MockitoExtension.class)
//class CategoryServiceTest {
//
//    @InjectMocks
//    private CategoryService categoryService;
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Test
//    @DisplayName("Deve criar categoria com sucesso quando usuário existe")
//    void createCategorySuccess() {
//        CategoryCreateRequestDTO dto = new CategoryCreateRequestDTO("Lazer", "DESPESA", 1L);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
//
//        categoryService.create(dto);
//
//        verify(categoryRepository, times(1)).save(any(Category.class));
//    }
//
//    @Test
//    @DisplayName("Deve lançar erro ao tentar criar categoria para usuário inexistente")
//    void createCategoryUserNotFound() {
//        CategoryCreateRequestDTO dto = new CategoryCreateRequestDTO("Lazer", "DESPESA", 50L);
//
//        when(userRepository.findById(50L)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(EntityNotFoundException.class, () -> {
//            categoryService.create(dto);
//        });
//
//        verify(categoryRepository, never()).save(any());
//    }
//}