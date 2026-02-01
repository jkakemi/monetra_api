package com.finance.api.usecases;

import com.finance.api.application.gateways.CategoryGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.application.usecases.category.CreateCategory;
import com.finance.api.application.usecases.category.DeleteCategory;
import com.finance.api.application.usecases.category.UpdateCategory;
import com.finance.api.domain.category.Category;
import com.finance.api.domain.transaction.TransactionType;
import com.finance.api.domain.user.User;
import com.finance.api.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryUseCasesTest {

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private UserGateway userGateway;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = new User("123.456.789-00", "João", "joao@email.com", "senha123", UserRole.USER);
        user.setId(1L);

        category = new Category(1L, "Alimentação", TransactionType.DESPESA, 1L);
        category.setId(1L);
    }

    @Nested
    @DisplayName("CreateCategory Tests")
    class CreateCategoryTests {

        private CreateCategory createCategory;

        @BeforeEach
        void setUp() {
            createCategory = new CreateCategory(categoryGateway, userGateway);
        }

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategorySuccessfully() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.existsByNameAndUserId("Transporte", 1L)).thenReturn(false);
            when(categoryGateway.create(any(Category.class))).thenAnswer(i -> {
                Category c = i.getArgument(0);
                c.setId(2L);
                return c;
            });

            Category result = createCategory.execute("joao@email.com", "Transporte", "DESPESA");

            assertNotNull(result);
            assertEquals("Transporte", result.getName());
            assertEquals(TransactionType.DESPESA, result.getType());
            verify(categoryGateway, times(1)).create(any(Category.class));
        }

        @Test
        @DisplayName("Should throw exception when category already exists")
        void shouldThrowExceptionWhenCategoryExists() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.existsByNameAndUserId("Alimentação", 1L)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                createCategory.execute("joao@email.com", "Alimentação", "DESPESA")
            );

            verify(categoryGateway, never()).create(any());
        }
    }

    @Nested
    @DisplayName("UpdateCategory Tests")
    class UpdateCategoryTests {

        private UpdateCategory updateCategory;

        @BeforeEach
        void setUp() {
            updateCategory = new UpdateCategory(categoryGateway, userGateway);
        }

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.findById(1L)).thenReturn(Optional.of(category));
            when(categoryGateway.existsByNameAndUserId("Novo Nome", 1L)).thenReturn(false);

            assertDoesNotThrow(() -> updateCategory.execute(
                    "joao@email.com", 1L, "Novo Nome", "RECEITA"
            ));

            verify(categoryGateway, times(1)).update(any(Category.class));
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> updateCategory.execute(
                    "joao@email.com", 99L, "Novo Nome", "RECEITA"
            ));

            verify(categoryGateway, never()).update(any());
        }

        @Test
        @DisplayName("Should throw exception when new name already exists")
        void shouldThrowExceptionWhenNameExists() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.findById(1L)).thenReturn(Optional.of(category));
            when(categoryGateway.existsByNameAndUserId("Transporte", 1L)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> updateCategory.execute(
                    "joao@email.com", 1L, "Transporte", "RECEITA"
            ));

            verify(categoryGateway, never()).update(any());
        }

        @Test
        @DisplayName("Should throw exception when category does not belong to user")
        void shouldThrowExceptionWhenCategoryNotBelongsToUser() {
            Category otherCategory = new Category(99L, "Outra", TransactionType.DESPESA, 99L);
            otherCategory.setId(1L);

            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.findById(1L)).thenReturn(Optional.of(otherCategory));
            when(categoryGateway.existsByNameAndUserId("Novo Nome", 1L)).thenReturn(false);

            assertThrows(IllegalArgumentException.class, () -> updateCategory.execute(
                    "joao@email.com", 1L, "Novo Nome", "RECEITA"
            ));

            verify(categoryGateway, never()).update(any());
        }

        @Test
        @DisplayName("Should update category without changing type")
        void shouldUpdateCategoryWithoutType() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.findById(1L)).thenReturn(Optional.of(category));
            when(categoryGateway.existsByNameAndUserId("Novo Nome", 1L)).thenReturn(false);

            assertDoesNotThrow(() -> updateCategory.execute(
                    "joao@email.com", 1L, "Novo Nome", null
            ));

            verify(categoryGateway, times(1)).update(any(Category.class));
        }
    }

    @Nested
    @DisplayName("DeleteCategory Tests")
    class DeleteCategoryTests {

        private DeleteCategory deleteCategory;

        @BeforeEach
        void setUp() {
            deleteCategory = new DeleteCategory(categoryGateway, userGateway);
        }

        @Test
        @DisplayName("Should delete category successfully")
        void shouldDeleteCategorySuccessfully() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.findById(1L)).thenReturn(Optional.of(category));

            assertDoesNotThrow(() -> deleteCategory.execute("joao@email.com", 1L));

            verify(categoryGateway, times(1)).delete(1L);
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> deleteCategory.execute("joao@email.com", 99L));

            verify(categoryGateway, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw exception when category does not belong to user")
        void shouldThrowExceptionWhenCategoryNotBelongsToUser() {
            Category otherCategory = new Category(99L, "Outra", TransactionType.DESPESA, 99L);
            otherCategory.setId(1L);

            when(userGateway.findByEmail("joao@email.com")).thenReturn(user);
            when(categoryGateway.findById(1L)).thenReturn(Optional.of(otherCategory));

            assertThrows(IllegalArgumentException.class, () -> deleteCategory.execute("joao@email.com", 1L));

            verify(categoryGateway, never()).delete(any());
        }
    }
}
