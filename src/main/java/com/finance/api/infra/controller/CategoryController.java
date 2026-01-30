package com.finance.api.infra.controller;

import com.finance.api.application.usecases.category.*;
import com.finance.api.domain.category.Category;
import com.finance.api.infra.controller.dto.CategoryRequestDTO;
import com.finance.api.infra.controller.dto.CategoryResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CreateCategory createCategory;
    private final ListMyCategories listMyCategories;
    private final UpdateCategory updateCategory;
    private final DeleteCategory deleteCategory;

    public CategoryController(CreateCategory createCategory, ListMyCategories listMyCategories, UpdateCategory updateCategory, DeleteCategory deleteCategory) {
        this.createCategory = createCategory;
        this.listMyCategories = listMyCategories;
        this.updateCategory = updateCategory;
        this.deleteCategory = deleteCategory;
    }

    private String getLoggedEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody CategoryRequestDTO dto) {
        Category cat = createCategory.execute(getLoggedEmail(), dto.name(), dto.type());
        return ResponseEntity.ok(new CategoryResponseDTO(cat.getId(), cat.getName(), cat.getType().toString()));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> list() {
        List<Category> categories = listMyCategories.execute(getLoggedEmail());

        List<CategoryResponseDTO> response = categories.stream()
                .map(c -> new CategoryResponseDTO(c.getId(), c.getName(), c.getType().toString()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CategoryRequestDTO dto) {
        updateCategory.execute(getLoggedEmail(), id, dto.name(), dto.type());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteCategory.execute(getLoggedEmail(), id);
        return ResponseEntity.noContent().build();
    }
}