package com.healthcare.platform.service;

import com.healthcare.platform.dto.MedicineOrderItemRequest;
import com.healthcare.platform.dto.MedicineOrderRequest;
import com.healthcare.platform.dto.MedicineOrderResponse;
import com.healthcare.platform.dto.MedicineResponse;
import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.MedicineOrder;
import com.healthcare.platform.model.MedicineOrderItem;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.MedicineOrderRepository;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Sprint 2 — Medicine Ordering (patient orders from a pharmacy's catalog). */
@Service
public class MedicineOrderService {
    private final MedicineOrderRepository orders;
    private final MedicineRepository medicines;
    private final UserRepository users;

    public MedicineOrderService(MedicineOrderRepository orders, MedicineRepository medicines, UserRepository users) {
        this.orders = orders;
        this.medicines = medicines;
        this.users = users;
    }

    public List<MedicineResponse> catalog() {
        return medicines.findByActiveTrueOrderByNameAsc().stream()
                .map(MedicineResponse::from)
                .toList();
    }

    @Transactional
    public MedicineOrderResponse placeOrder(User patient, MedicineOrderRequest request) {
        requireRole(patient, UserRole.PATIENT);
        User pharmacy = users.findById(request.pharmacyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacy not found"));
        if (pharmacy.getRole() != UserRole.PHARMACY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected user is not a pharmacy");
        }

        MedicineOrder order = new MedicineOrder();
        order.setPatient(patient);
        order.setPharmacy(pharmacy);
        order.setDeliveryAddress(request.deliveryAddress());

        BigDecimal total = BigDecimal.ZERO;
        for (MedicineOrderItemRequest itemRequest : request.items()) {
            Medicine medicine = medicines.findById(itemRequest.medicineId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found: " + itemRequest.medicineId()));
            if (medicine.getStockQuantity() < itemRequest.quantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock for " + medicine.getName());
            }
            medicine.setStockQuantity(medicine.getStockQuantity() - itemRequest.quantity());
            medicines.save(medicine);

            MedicineOrderItem item = new MedicineOrderItem(medicine, itemRequest.quantity(), medicine.getPrice());
            order.addItem(item);
            total = total.add(item.lineTotal());
        }
        order.setTotalAmount(total);

        return MedicineOrderResponse.from(orders.save(order));
    }

    public List<MedicineOrderResponse> myOrders(User patient) {
        requireRole(patient, UserRole.PATIENT);
        return orders.findByPatientIdOrderByCreatedAtDesc(patient.getId()).stream()
                .map(MedicineOrderResponse::from)
                .toList();
    }

    public List<MedicineOrderResponse> pharmacyOrders(User pharmacy) {
        requireRole(pharmacy, UserRole.PHARMACY);
        return orders.findByPharmacyIdOrderByCreatedAtDesc(pharmacy.getId()).stream()
                .map(MedicineOrderResponse::from)
                .toList();
    }

    public MedicineOrderResponse updateStatus(User pharmacy, Long orderId, String status) {
        requireRole(pharmacy, UserRole.PHARMACY);
        MedicineOrder order = orders.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getPharmacy().getId().equals(pharmacy.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }
        String normalized = status.toUpperCase();
        if (!List.of("CONFIRMED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED").contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        order.setStatus(normalized);
        return MedicineOrderResponse.from(orders.save(order));
    }

    private void requireRole(User user, UserRole role) {
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This action requires the " + role + " role");
        }
    }
}
