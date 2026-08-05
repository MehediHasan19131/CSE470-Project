package com.healthcare.platform.dto;

import com.healthcare.platform.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 */
public record OrderResponse(
        Long id,
        String status,
        BigDecimal totalAmount,
        String deliveryAddress,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDeliveryAddress(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
