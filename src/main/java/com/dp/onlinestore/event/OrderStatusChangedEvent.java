package com.dp.onlinestore.event;

import com.dp.onlinestore.entity.OrderStatus;

public record OrderStatusChangedEvent(Long orderId, String email, OrderStatus status) {
}