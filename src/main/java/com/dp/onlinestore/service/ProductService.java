package com.dp.onlinestore.service;

import com.dp.onlinestore.dto.ProductCreateRequest;
import com.dp.onlinestore.dto.ProductResponse;
import com.dp.onlinestore.dto.ProductUpdateRequest;
import com.dp.onlinestore.entity.Category;
import com.dp.onlinestore.entity.Product;
import com.dp.onlinestore.exception.DuplicateResourceException;
import com.dp.onlinestore.exception.ResourceNotFoundException;
import com.dp.onlinestore.exception.VersionConflictException;
import com.dp.onlinestore.repository.CategoryRepository;
import com.dp.onlinestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        } else {
            products = productRepository.findByIsActiveTrue(pageable);
        }

        return products.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар с id " + id + " не найден"));
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        // Проверка на дубликат SKU
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Товар с SKU " + request.getSku() + " уже существует");
        }

        // Проверка категории
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Категория с id " + request.getCategoryId() + " не найдена"));

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setIsActive(true);
        product.setVersion(0L);  // ← Явно устанавливаем версию

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар с id " + id + " не найден"));

        // Проверка версии (оптимистичная блокировка)
        if (!product.getVersion().equals(request.getVersion())) {
            throw new VersionConflictException(
                    "Товар был изменен другим пользователем. Пожалуйста, обновите данные и попробуйте снова.");
        }

        // Проверка на дубликат SKU (если SKU изменился)
        if (!product.getSku().equals(request.getSku()) &&
                productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Товар с SKU " + request.getSku() + " уже существует");
        }

        // Проверка категории
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Категория с id " + request.getCategoryId() + " не найдена"));

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар с id " + id + " не найден"));

        // Soft delete
        product.setIsActive(false);
        productRepository.save(product);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory().getId())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}