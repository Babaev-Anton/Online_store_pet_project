package com.dp.onlinestore.service;

import com.dp.onlinestore.dto.*;
import com.dp.onlinestore.entity.Cart;
import com.dp.onlinestore.entity.CartItem;
import com.dp.onlinestore.entity.Product;
import com.dp.onlinestore.entity.User;
import com.dp.onlinestore.exception.ResourceNotFoundException;
import com.dp.onlinestore.repository.CartRepository;
import com.dp.onlinestore.repository.ProductRepository;
import com.dp.onlinestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        Cart cart = cartRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Корзина пуста"));
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Ленивое создание корзины
        Cart cart = cartRepository.findByUserEmail(email).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });

        // Ищем, есть ли уже такой товар в корзине
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(String email, Long productId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not in cart"));

        item.setQuantity(request.getQuantity());
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Transactional
    public void removeItem(String email, Long productId) {
        Cart cart = cartRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    // Вспомогательный метод для маппинга Entity -> DTO
    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> {
                    BigDecimal subtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return CartItemResponse.builder()
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .price(item.getProduct().getPrice())
                            .quantity(item.getQuantity())
                            .subtotal(subtotal)
                            .build();
                })
                .toList();

        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .build();
    }
}