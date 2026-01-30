package com.finance.api.infra.persistence;

import com.finance.api.domain.account.AccountType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "account_number")
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal balance;
    private BigDecimal creditLimit;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
