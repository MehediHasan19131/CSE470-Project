package com.healthcare.platform.service;

import com.healthcare.platform.dto.PlaceOrderRequest;
import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.Order;
import com.healthcare.platform.model.OrderItem;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.OrderRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 * Place Order + order history.
 */
@Service
public class OrderService {
    private final OrderRepository orders;
    private final MedicineRepository medicines;

    public OrderService(OrderRepository orders, MedicineRepository medicines) {
        this.orders = orders;
        this.medicines = medicines;
    }

    // Place Order
    @Transactional
    public Order placeOrder(User patient, PlaceOrderRequest request) {
        Medicine medicine = medicines.findById(request.medicineId())
                .filter(Medicine::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found with id: " + request.medicineId()));

        if (request.quantity() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
        }
        if (medicine.getStockQuantity() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough stock for " + medicine.getName() + " (available: " + medicine.getStockQuantity() + ")");
        }

        medicine.setStockQuantity(medicine.getStockQuantity() - request.quantity());
        medicines.save(medicine);

        Order order = new Order();
        order.setPatient(patient);
        order.setDeliveryAddress(request.deliveryAddress());

        OrderItem item = new OrderItem(medicine, request.quantity(), medicine.getPrice());
        order.addItem(item);
        order.setTotalAmount(item.getSubtotal());

        return orders.save(order);
    }

    public List<Order> getOrdersForPatient(Long patientId) {
        return orders.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public Order getOrderById(Long id, User requester) {
        Order order = orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));

        boolean isOwner = order.getPatient() != null && order.getPatient().getId().equals(requester.getId());
        boolean isStaff = requester.getRole() == UserRole.ADMIN || requester.getRole() == UserRole.PHARMACY;
        if (!isOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this order");
        }
        return order;
    }
}
