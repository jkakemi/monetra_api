package com.finance.api.infra.gateways;

import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.domain.category.Category;
import com.finance.api.infra.persistence.CategoryEntity;
import com.finance.api.infra.persistence.SpringCategoryRepository;
import com.finance.api.infra.persistence.SpringUserRepository;
import com.finance.api.infra.persistence.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoryRepositoryJpa implements CategoryGateway {

    private final SpringCategoryRepository springCategoryRepository;
    private final SpringUserRepository springUserRepository;
    private final CategoryEntityMapper mapper;

    public CategoryRepositoryJpa(SpringCategoryRepository springCategoryRepository, SpringUserRepository springUserRepository, CategoryEntityMapper mapper) {
        this.springCategoryRepository = springCategoryRepository;
        this.springUserRepository = springUserRepository;
        this.mapper = mapper;
    }

    @Override
    public Category create(Category category) {
        return saveOrUpdate(category);
    }

    @Override
    public Category update(Category category) {
        return saveOrUpdate(category);
    }

    private Category saveOrUpdate(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        UserEntity user = springUserRepository.findById(category.getUserId()).orElseThrow();
        entity.setUser(user);

        return mapper.toDomain(springCategoryRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        springCategoryRepository.deleteById(id);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return springCategoryRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Category> listByUserId(Long userId) {
        return springCategoryRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndUserId(String name, Long userId) {
        return springCategoryRepository.existsByNameIgnoreCaseAndUserId(name, userId);
    }

}