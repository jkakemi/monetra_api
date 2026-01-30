package com.finance.api.application.usecases.category;

import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.transaction.TransactionType;
import com.finance.api.domain.user.User;

public class UpdateCategory {
    private final CategoryGateway categoryGateway;
    private final UserGateway userGateway;

    public UpdateCategory(CategoryGateway categoryGateway, UserGateway userGateway) {
        this.categoryGateway = categoryGateway;
        this.userGateway = userGateway;
    }

    public void execute(String userEmail, Long categoryId, String newName, String newTypeStr) {
        User user = userGateway.findByEmail(userEmail);
        Category category = categoryGateway.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (categoryGateway.existsByNameAndUserId(newName, user.getId())) {
            throw new IllegalArgumentException("Category already exists");
        }
        if (!category.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("This category does not exist");
        }

        TransactionType newType = (newTypeStr != null) ? TransactionType.valueOf(newTypeStr.toUpperCase()) : null;
        category.update(newName, newType);

        categoryGateway.update(category);
    }
}