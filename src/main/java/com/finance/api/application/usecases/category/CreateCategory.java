package com.finance.api.application.usecases.category;

import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.transaction.TransactionType;
import com.finance.api.domain.user.User;

public class CreateCategory {
    private final CategoryGateway categoryGateway;
    private final UserGateway userGateway;

    public CreateCategory(CategoryGateway categoryGateway, UserGateway userGateway) {
        this.categoryGateway = categoryGateway;
        this.userGateway = userGateway;
    }

    public Category execute(String userEmail, String name, String typeStr) {
        User user = userGateway.findByEmail(userEmail);

        if (categoryGateway.existsByNameAndUserId(name, user.getId())) {
            throw new IllegalArgumentException("This category already exists");
        }

        TransactionType type = TransactionType.valueOf(typeStr.toUpperCase());

        Category category = new Category(null, name, type, user.getId());

        return categoryGateway.create(category);
    }
}