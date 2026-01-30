package com.finance.api.config;

import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.category.CreateCategory;
import com.finance.api.application.usecases.category.DeleteCategory;
import com.finance.api.application.usecases.category.ListMyCategories;
import com.finance.api.application.usecases.category.UpdateCategory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryConfig {
    @Bean
    CreateCategory createCategory(CategoryGateway categoryGateway, UserGateway userGateway) { return new CreateCategory(categoryGateway, userGateway); }

    @Bean
    ListMyCategories listMyCategories(CategoryGateway categoryGateway, UserGateway userGateway) { return new ListMyCategories(categoryGateway, userGateway); }

    @Bean
    UpdateCategory updateCategory(CategoryGateway categoryGateway, UserGateway userGateway) { return new UpdateCategory(categoryGateway, userGateway); }

    @Bean
    DeleteCategory deleteCategory(CategoryGateway categoryGateway, UserGateway userGateway) { return new DeleteCategory(categoryGateway, userGateway); }
}
