package com.finance.api.infra.gateways;

import com.finance.api.domain.category.Category;
import com.finance.api.infra.persistence.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityMapper {

    public CategoryEntity toEntity(Category domain) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setType(domain.getType());
        return entity;
    }

    public Category toDomain(CategoryEntity entity) {
        Long userId = (entity.getUser() != null) ? entity.getUser().getId() : null;
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                userId
        );
    }
}