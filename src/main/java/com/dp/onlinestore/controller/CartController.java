package com.dp.onlinestore.controller;

import com.dp.onlinestore.dto.AddCartItemRequest;
import com.dp.onlinestore.dto.CartResponse;
import com.dp.onlinestore.dto.UpdateCartItemRequest;
import com.dp.onlinestore.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCart(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Authentication authentication, @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(authentication.getName(), request));
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(authentication.getName(), productId, request));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(Authentication authentication, @PathVariable Long productId) {
        cartService.removeItem(authentication.getName(), productId);
        return ResponseEntity.noContent().build();
    }
}