package com.dp.onlinestore.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductPatchRequest {
    private BigDecimal price;
    private Integer stockQuantity;
}