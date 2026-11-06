package com.dp.onlinestore.service;

import com.dp.onlinestore.dto.OrderItemResponse;
import com.dp.onlinestore.dto.OrderResponse;
import com.dp.onlinestore.entity.*;
import com.dp.onlinestore.event.OrderStatusChangedEvent;
import com.dp.onlinestore.exception.EmptyCartException;
import com.dp.onlinestore.exception.InsufficientStockException;
import com.dp.onlinestore.exception.ResourceNotFoundException;
import com.dp.onlinestore.repository.CartRepository;
import com.dp.onlinestore.repository.OrderRepository;
import com.dp.onlinestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createOrderFromCart(String email) {
        Cart cart = cartRepository.findByUserEmail(email)
                .orElseThrow(() -> new EmptyCartException("Корзина не найдена"));

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Корзина пуста");
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Проверка остатков на складе
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Недостаточно товара на складе: " + product.getName() +
                        ". Доступно: " + product.getStockQuantity());
            }

            // Уменьшаем остаток на складе (оптимистичная блокировка сработает при save)
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Создаем позицию заказа со снимком цены
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();
            orderItems.add(orderItem);

            // Считаем общую сумму
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Сохраняем заказ
        Order savedOrder = orderRepository.save(order);

        // Очищаем корзину
        cart.getItems().clear();
        cartRepository.save(cart);

        // Публикуем событие о создании заказа
        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                savedOrder.getId(),
                savedOrder.getUser().getEmail(),
                savedOrder.getStatus()
        ));

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, String email, boolean isAdmin) {
        Order order;
        if (isAdmin) {
            // Админ может смотреть любой заказ
            order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));
        } else {
            // Обычный пользователь — только свой (Защита от IDOR)
            order = orderRepository.findByIdAndUserEmail(id, email)
                    .orElseThrow(() -> new AccessDeniedException("У вас нет доступа к этому заказу"));
        }
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .subtotal(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}