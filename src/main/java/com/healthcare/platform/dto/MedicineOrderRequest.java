package com.healthcare.platform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MedicineOrderRequest(
        @NotNull Long pharmacyId,
        @NotEmpty @Valid List<MedicineOrderItemRequest> items,
        String deliveryAddress
) {
}
