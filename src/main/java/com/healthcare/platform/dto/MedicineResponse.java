package com.healthcare.platform.dto;

import com.healthcare.platform.model.Medicine;
import java.math.BigDecimal;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 */
public record MedicineResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        boolean inStock
) {
    public static MedicineResponse from(Medicine medicine) {
        return new MedicineResponse(
                medicine.getId(),
                medicine.getName(),
                medicine.getDescription(),
                medicine.getPrice(),
                medicine.getStockQuantity(),
                medicine.getStockQuantity() > 0
        );
    }
}
