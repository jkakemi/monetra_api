//package com.finance.api.service;
//
//import com.finance.api.dto.request.AccountCreateRequestDTO;
//import com.finance.api.dto.response.AccountResponseDTO;
//import com.finance.api.model.Account;
//import com.finance.api.model.AccountType;
//import com.finance.api.model.User;
//import com.finance.api.repository.AccountRepository;
//import com.finance.api.repository.UserRepository;
//import com.finance.api.service.mockapi.BankMockApiService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AccountServiceTest {
//
//    @InjectMocks
//    private AccountService accountService;
//
//    @Mock
//    private AccountRepository accountRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private BankMockApiService bankMockApiService;
//
//    @Test
//    @DisplayName("Deve criar conta BANK buscando saldo na API externa")
//    void createBankAccountWithIntegration() {
//        AccountCreateRequestDTO data = new AccountCreateRequestDTO(
//                "Nubank",
//                BigDecimal.ZERO,
//                BigDecimal.ZERO,
//                "1234-5",
//                "BANCO",
//                1L
//        );
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
//
//        when(bankMockApiService.getMoneyFromBankAccount("12345-6"))
//                .thenReturn(new BigDecimal("500.00"));
//
//        accountService.create(data);
//
//        verify(bankMockApiService, times(1)).getMoneyFromBankAccount("12345-6");
//        verify(accountRepository, times(1)).save(any(Account.class));
//    }
//
//    @Test
//    @DisplayName("Deve criar conta CASH usando saldo manual (sem chamar API)")
//    void createCashAccountManualBalance() {
//        // CENÁRIO
//        AccountCreateRequestDTO data = new AccountCreateRequestDTO(
//                "Carteira",
//                new BigDecimal("50.00"),
//                BigDecimal.ZERO,
//                "1234-5",
//                "FISICO",
//                1L
//        );
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
//
//        accountService.create(data);
//
//        verify(bankMockApiService, never()).getMoneyFromBankAccount(any());
//
//        verify(accountRepository, times(1)).save(any(Account.class));
//    }
//
//    @Test
//    @DisplayName("Deve listar contas apenas do usuário específico")
//    void listAccountsByUser() {
//        Long userId = 1L;
//        Account account = new Account();
//        account.setName("Conta teste");
//        account.setType(AccountType.FISICO);
//        account.setMoney(BigDecimal.ZERO);
//
//        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));
//
//        List<AccountResponseDTO> result = accountService.listByUserId(userId);
//
//        Assertions.assertEquals(1, result.size());
//    }
//
//
//}