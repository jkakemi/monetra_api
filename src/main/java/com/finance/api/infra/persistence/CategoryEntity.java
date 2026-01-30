package com.finance.api.infra.persistence;

import com.finance.api.domain.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class CategoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public CategoryEntity(Long id, String name, TransactionType type, UserEntity user) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.user = user;
    }
}