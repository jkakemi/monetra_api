//package com.finance.api.service;
//
//import com.finance.api.dto.request.TransactionCreateRequestDTO;
//import com.finance.api.infra.exception.EntityNotFoundException;
//import com.finance.api.model.Account;
//import com.finance.api.model.Category;
//import com.finance.api.model.Transaction;
//import com.finance.api.model.User;
//import com.finance.api.repository.AccountRepository;
//import com.finance.api.repository.CategoryRepository;
//import com.finance.api.repository.TransactionRepository;
//import com.finance.api.repository.UserRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.crossstore.ChangeSetPersister;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TransactionServiceTest {
//
//    @InjectMocks
//    private TransactionService transactionService;
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private AccountRepository accountRepository;
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @Mock
//    private QuoteService quoteService;
//
//    @Test
//    @DisplayName("Deve criar transação com sucesso quando estiver tudo correto (BRL)")
//    void createTransactionSucess(){
//        Long userId = 1L;
//        Long accountId = 1L;
//        Long categoryId = 1L;
//
//        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO(
//                "Testando BRL",
//                new BigDecimal("50.00"),
//                "BRL",
//                "2026-01-24",
//                accountId,
//                categoryId,
//                userId
//        );
//
//        User user = new User();
//        Account account = new Account();
//        Category category = new Category();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
//
//        when(quoteService.getQuote(any(), any())).thenReturn(BigDecimal.ONE);
//        transactionService.create(dto);
//
//        verify(transactionRepository, times(1)).save(any(Transaction.class));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando o user não existir")
//    void createTransactionUserNotFound(){
//        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO(
//                "Testando error not found",
//                BigDecimal.TEN,
//                "BRL",
//                "2026-01-24",
//                1L,
//                1L,
//                50L
//        );
//
//        when(userRepository.findById(50L)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(EntityNotFoundException.class, () -> {
//            transactionService.create(dto);
//        });
//
//        verify(transactionRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Deve calcular conversão quando moeda for USD")
//    void createTransactionUSD(){
//        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO(
//                "Testando USD",
//                new BigDecimal("20.00"),
//                "USD",
//                "2026-01-24",
//                1L,
//                1L,
//                1L
//        );
//
//        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
//        when(accountRepository.findById(any())).thenReturn(Optional.of(new Account()));
//        when(categoryRepository.findById(any())).thenReturn(Optional.of(new Category()));
//
//        when(quoteService.getQuote(eq("USD"), any(LocalDate.class)))
//                .thenReturn(new BigDecimal("5.00"));
//
//        transactionService.create(dto);
//        verify(quoteService, times(1)).getQuote(eq("USD"), any(LocalDate.class));
//        verify(transactionRepository, times(1)).save(any(Transaction.class));
//    }
//
//
//
//}