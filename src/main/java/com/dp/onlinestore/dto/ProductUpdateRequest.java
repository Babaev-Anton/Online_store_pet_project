package com.dp.onlinestore.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {

    @NotBlank(message = "SKU не может быть пустым")
    @Size(max = 64, message = "SKU не может быть длиннее 64 символов")
    private String sku;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 200, message = "Название не может быть длиннее 200 символов")
    private String name;

    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    @Digits(integer = 8, fraction = 2, message = "Цена должна иметь не более 2 знаков после запятой")
    private BigDecimal price;

    @NotNull(message = "Количество на складе обязательно")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer stockQuantity;

    @NotNull(message = "ID категории обязателен")
    private Long categoryId;

    @NotNull(message = "Версия обязательна для оптимистичной блокировки")
    private Long version;
}
