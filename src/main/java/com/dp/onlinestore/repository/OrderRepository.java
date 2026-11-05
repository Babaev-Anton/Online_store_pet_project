package com.dp.onlinestore.repository;

import com.dp.onlinestore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserEmailOrderByCreatedAtDesc(String email);
    Optional<Order> findByIdAndUserEmail(Long id, String email);
}