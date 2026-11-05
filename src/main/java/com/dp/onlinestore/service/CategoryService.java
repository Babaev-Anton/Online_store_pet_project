package com.dp.onlinestore.service;

import com.dp.onlinestore.dto.CategoryCreateRequest;
import com.dp.onlinestore.dto.CategoryResponse;
import com.dp.onlinestore.entity.Category;
import com.dp.onlinestore.exception.CategoryHasProductsException;
import com.dp.onlinestore.exception.DuplicateResourceException;
import com.dp.onlinestore.exception.ResourceNotFoundException;
import com.dp.onlinestore.repository.CategoryRepository;
import com.dp.onlinestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория с id " + id + " не найдена"));
        return mapToResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Категория с slug " + request.getSlug() + " уже существует");
        }

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Категория с названием '" + request.getName() + "' уже существует");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());

        Category savedCategory = categoryRepository.save(category);
        log.info("Категория '{}' создана (id={})", savedCategory.getName(), savedCategory.getId());
        return mapToResponse(savedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория с id " + id + " не найдена"));

        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new CategoryHasProductsException(
                    "Нельзя удалить категорию '" + category.getName() + "', так как в ней есть " + productCount + " товар(ов)");
        }

        categoryRepository.delete(category);
        log.info("Категория '{}' (id={}) успешно удалена", category.getName(), id);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .build();
    }
}