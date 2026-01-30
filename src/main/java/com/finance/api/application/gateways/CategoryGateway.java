package com.finance.api.application.gateways;

import com.finance.api.domain.category.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryGateway {
    Category create(Category category);
    Category update(Category category);
    void delete(Long id);
    Optional<Category> findById(Long id);
    List<Category> listByUserId(Long userId);
    boolean existsByNameAndUserId(String name, Long userId);
}