package com.finance.api.application.usecases.category;

import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.user.User;
import java.util.List;

public class ListMyCategories {
    private final CategoryGateway categoryGateway;
    private final UserGateway userGateway;

    public ListMyCategories(CategoryGateway categoryGateway, UserGateway userGateway) {
        this.categoryGateway = categoryGateway;
        this.userGateway = userGateway;
    }

    public List<Category> execute(String emailOwner) {
        User user = userGateway.findByEmail(emailOwner);
        return categoryGateway.listByUserId(user.getId());
    }
}