package com.dp.onlinestore.dto;

import com.dp.onlinestore.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "Статус не может быть пустым")
    private OrderStatus status;
}