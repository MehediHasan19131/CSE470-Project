package com.healthcare.platform.dto;

import java.math.BigDecimal;

import com.healthcare.platform.model.OrderItem;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 */
public record OrderItemResponse(
        Long medicineId,
        String medicineName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getMedicine().getId(),
                item.getMedicine().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
