package com.healthcare.platform.controller;

import com.healthcare.platform.model.Order;
import com.healthcare.platform.model.User;
import com.healthcare.platform.dto.OrderResponse;
import com.healthcare.platform.dto.PlaceOrderRequest;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 * Place Order + order history.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderApiController {
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public OrderApiController(OrderService orderService, CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    // Place Order
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request, Authentication authentication) {
        User patient = currentUserService.get(authentication);
        Order order = orderService.placeOrder(patient, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    // My order history
    @GetMapping
    public List<OrderResponse> getMyOrders(Authentication authentication) {
        User patient = currentUserService.get(authentication);
        return orderService.getOrdersForPatient(patient.getId()).stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id, Authentication authentication) {
        User requester = currentUserService.get(authentication);
        return OrderResponse.from(orderService.getOrderById(id, requester));
    }
}
