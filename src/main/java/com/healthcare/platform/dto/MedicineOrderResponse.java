package com.healthcare.platform.dto;

import com.healthcare.platform.model.MedicineOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MedicineOrderResponse(
        Long id,
        String status,
        Long pharmacyId,
        String pharmacyName,
        Long patientId,
        String patientName,
        String deliveryAddress,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MedicineOrderItemResponse> items
) {
    public static MedicineOrderResponse from(MedicineOrder order) {
        return new MedicineOrderResponse(
                order.getId(),
                order.getStatus(),
                order.getPharmacy().getId(),
                order.getPharmacy().getFullName(),
                order.getPatient().getId(),
                order.getPatient().getFullName(),
                order.getDeliveryAddress(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getItems().stream().map(MedicineOrderItemResponse::from).toList()
        );
    }
}
