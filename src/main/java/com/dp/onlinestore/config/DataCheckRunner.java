package com.dp.onlinestore.config;

import com.dp.onlinestore.entity.Category;
import com.dp.onlinestore.entity.Product;
import com.dp.onlinestore.repository.CategoryRepository;
import com.dp.onlinestore.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataCheckRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataCheckRunner.class);
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataCheckRunner(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("🔄 Начинаю проверку работы с БД...");

            // Проверяем, есть ли уже данные
            long categoryCount = categoryRepository.count();
            log.info("Текущее количество категорий: {}", categoryCount);

            if (categoryCount == 0) {
                // Создаем категорию
                Category category = new Category();
                category.setName("Электроника");
                category.setSlug("electronics");
                category = categoryRepository.save(category);
                log.info("✅ Категория создана: {} (id={})", category.getName(), category.getId());

                // Создаем товар
                Product product = new Product();
                product.setSku("SKU-001");
                product.setName("Смартфон");
                product.setDescription("Современный смартфон");
                product.setPrice(new BigDecimal("49999.99"));
                product.setStockQuantity(10);
                product.setCategory(category);
                product.setIsActive(true);
                product = productRepository.save(product);
                log.info("✅ Товар создан: {} (id={})", product.getName(), product.getId());
            }

            // Читаем из БД
            Category savedCategory = categoryRepository.findAll().stream().findFirst().orElseThrow();
            log.info("✅ Категория прочитана из БД: {}", savedCategory.getName());

            Product savedProduct = productRepository.findAll().stream().findFirst().orElseThrow();
            log.info("✅ Товар прочитан из БД: {} (категория: {}, цена: {})",
                    savedProduct.getName(),
                    savedProduct.getCategory().getName(),
                    savedProduct.getPrice());

            log.info("✅ Проверка работы с БД завершена успешно!");

        } catch (Exception e) {
            log.error("❌ Ошибка при работе с БД: {}", e.getMessage(), e);
        }
    }
}

