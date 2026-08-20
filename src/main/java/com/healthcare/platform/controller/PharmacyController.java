package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.MedicineService;
import com.healthcare.platform.service.OrderService;
import java.math.BigDecimal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * Pharmacy-owner catalogue management: each pharmacy manages its own medicines
 * (per-facility products). ADMIN-free — restricted to PHARMACY by SecurityConfig's
 * "/pharmacy/**" rule.
 */
@Controller
public class PharmacyController {

    private final MedicineService medicineService;
    private final CurrentUserService currentUserService;
    private final OrderService orderService;

    public PharmacyController(MedicineService medicineService, CurrentUserService currentUserService,
                              OrderService orderService) {
        this.medicineService = medicineService;
        this.currentUserService = currentUserService;
        this.orderService = orderService;
    }

    @GetMapping("/pharmacy/medicines")
    public String medicines(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("medicines", medicineService.myMedicines(user.getId()));
        return "pharmacy-medicines";
    }

    @PostMapping("/pharmacy/medicines")
    public String add(@RequestParam String name,
                      @RequestParam(required = false) String description,
                      @RequestParam BigDecimal price,
                      @RequestParam(defaultValue = "0") int stock,
                      Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            medicineService.addMedicine(user, name, description, price, stock);
            return "redirect:/pharmacy/medicines?saved";
        } catch (ResponseStatusException e) {
            return "redirect:/pharmacy/medicines?error";
        }
    }

    @PostMapping("/pharmacy/medicines/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam BigDecimal price,
                         @RequestParam(defaultValue = "0") int stock,
                         @RequestParam(defaultValue = "false") boolean active,
                         Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            medicineService.updateMedicine(user, id, price, stock, active);
            return "redirect:/pharmacy/medicines?saved";
        } catch (ResponseStatusException e) {
            return "redirect:/pharmacy/medicines?error";
        }
    }

    @GetMapping("/pharmacy/orders")
    public String orders(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getOrdersForPharmacy(user.getId()));
        return "pharmacy-orders";
    }

    @PostMapping("/pharmacy/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status,
                                    Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            orderService.updateStatus(user, id, status);
            return "redirect:/pharmacy/orders?updated";
        } catch (ResponseStatusException e) {
            return "redirect:/pharmacy/orders?error";
        }
    }
}
