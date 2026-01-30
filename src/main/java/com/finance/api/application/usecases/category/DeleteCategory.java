package com.finance.api.application.usecases.category;

import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.user.User;

public class DeleteCategory {
    private final CategoryGateway categoryGateway;
    private final UserGateway userGateway;

    public DeleteCategory(CategoryGateway categoryGateway, UserGateway userGateway) {
        this.categoryGateway = categoryGateway;
        this.userGateway = userGateway;
    }

    public void execute(String emailOwner, Long categoryId) {
        User user = userGateway.findByEmail(emailOwner);
        Category category = categoryGateway.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!category.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("This category does not exist");
        }

        categoryGateway.delete(categoryId);
    }
}