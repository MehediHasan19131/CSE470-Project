package com.healthcare.platform.controller;

import com.healthcare.platform.dto.MedicineOrderRequest;
import com.healthcare.platform.dto.MedicineOrderResponse;
import com.healthcare.platform.dto.MedicineOrderStatusUpdateRequest;
import com.healthcare.platform.dto.MedicineResponse;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.MedicineOrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Sprint 2 — Medicine Ordering + pharmacy services. */
@RestController
public class MedicineOrderApiController {
    private final MedicineOrderService medicineOrderService;
    private final CurrentUserService currentUserService;

    public MedicineOrderApiController(MedicineOrderService medicineOrderService, CurrentUserService currentUserService) {
        this.medicineOrderService = medicineOrderService;
        this.currentUserService = currentUserService;
    }

    // Sami's MedicineApiController already maps GET /api/medicines; an identical
    // pattern makes Spring fail at startup, so this member's copy is namespaced.
    @GetMapping("/api/mehedi/medicines")
    public List<MedicineResponse> catalog() {
        return medicineOrderService.catalog();
    }

    @PostMapping("/api/medicine-orders")
    public MedicineOrderResponse placeOrder(@Valid @RequestBody MedicineOrderRequest body, Authentication authentication) {
        User patient = currentUserService.get(authentication);
        return medicineOrderService.placeOrder(patient, body);
    }

    @GetMapping("/api/medicine-orders/me")
    public List<MedicineOrderResponse> myOrders(Authentication authentication) {
        return medicineOrderService.myOrders(currentUserService.get(authentication));
    }

    @GetMapping("/api/medicine-orders/pharmacy")
    public List<MedicineOrderResponse> pharmacyOrders(Authentication authentication) {
        return medicineOrderService.pharmacyOrders(currentUserService.get(authentication));
    }

    @PatchMapping("/api/medicine-orders/{orderId}/status")
    public MedicineOrderResponse updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody MedicineOrderStatusUpdateRequest body,
            Authentication authentication
    ) {
        User pharmacy = currentUserService.get(authentication);
        return medicineOrderService.updateStatus(pharmacy, orderId, body.status());
    }
}
