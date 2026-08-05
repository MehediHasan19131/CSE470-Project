package com.healthcare.platform.dto;

import com.healthcare.platform.model.MedicineOrderItem;
import java.math.BigDecimal;

public record MedicineOrderItemResponse(
        Long medicineId,
        String medicineName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
    public static MedicineOrderItemResponse from(MedicineOrderItem item) {
        return new MedicineOrderItemResponse(
                item.getMedicine().getId(),
                item.getMedicine().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.lineTotal()
        );
    }
}
