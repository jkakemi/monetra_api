//package com.finance.api.service;
//
//import com.finance.api.dto.request.UserRegisterRequestDTO;
//import com.finance.api.dto.request.UserUpdateRequestDTO;
//import com.finance.api.dto.response.UserResponseDTO;
//import com.finance.api.infra.exception.EntityNotFoundException;
//import com.finance.api.model.User;
//import com.finance.api.repository.UserRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.util.Assert;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @InjectMocks
//    private UserService userService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Test
//    @DisplayName("Deve salvar usuário com sucesso")
//    void createUserSuccess(){
//        //arrange
//        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("Julia", "julia@example.com", "123456");
//
//        // act
//        userService.create(dto);
//
//        // assert
//        verify(userRepository, times(1)).save(any(User.class));
//    }
//
//    @Test
//    @DisplayName("Deve retornar lista de usuários")
//    void findAllUsersSuccess(){
//        User user1 = new User();
//        user1.setId(1L);
//        user1.setName("Julia");
//        user1.setEmail("julia@example.com");
//        User user2 = new User();
//        user2.setId(2L);
//        user2.setName("Gabriel");
//        user2.setEmail("gabriel@example.com");
//
//        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
//
//        List<UserResponseDTO> result = userService.list();
//        Assertions.assertEquals(2, result.size());
//        Assertions.assertEquals("Julia", result.get(0).name());
//    }
//
//    @Test
//    @DisplayName("Deve atualizar usuário já existente")
//    void updateUserSuccess(){
//        Long id = 1L;
//        UserUpdateRequestDTO dto = new UserUpdateRequestDTO(id,
//                "Ana");
//
//        User user = new User();
//
//        when(userRepository.findById(id)).thenReturn(Optional.of(user));
//
//        userService.update(dto);
//
//        verify(userRepository, times(1)).save(user);
//        Assertions.assertEquals("Ana", user.getName());
//    }
//
//    @Test
//    @DisplayName("Deve lançar erro ao tentar atualizar usuário inexistente")
//    void updateUserNotFound() {
//        UserUpdateRequestDTO data = new UserUpdateRequestDTO(50L, "Teste inexistente");
//
//        when(userRepository.findById(50L)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(EntityNotFoundException.class, () -> {
//            userService.update(data);
//        });
//
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Deve lançar erro ao tentar deletar usuário inexistente")
//    void deleteUserNotFound() {
//        Long id = 50L;
//
//        when(userRepository.findById(id)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(EntityNotFoundException.class, () -> {
//            userService.delete(id);
//        });
//
//        verify(userRepository, never()).deleteById(any());
//    }
//}