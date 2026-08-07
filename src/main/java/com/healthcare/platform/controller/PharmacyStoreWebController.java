package com.healthcare.platform.controller;

import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.User;
import com.healthcare.platform.dto.PlaceOrderRequest;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.MedicineService;
import com.healthcare.platform.service.OrderService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 * Server-rendered (Thymeleaf + Bootstrap) pages:
 * - Pharmacy Store   -> /pharmacy-store
 * - Medicine Details -> /pharmacy-store/{id}
 * - Order Page       -> /orders
 */
@Controller
public class PharmacyStoreWebController {
    private final MedicineService medicineService;
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public PharmacyStoreWebController(MedicineService medicineService, OrderService orderService, CurrentUserService currentUserService) {
        this.medicineService = medicineService;
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/pharmacy-store")
    public String pharmacyStore(@RequestParam(required = false) String q, Authentication authentication, Model model) {
        addCurrentUser(authentication, model);

        boolean searched = q != null && !q.isBlank();
        List<Medicine> medicines = searched ? medicineService.searchMedicines(q) : medicineService.getAllMedicines();

        model.addAttribute("medicines", medicines);
        model.addAttribute("searched", searched);
        model.addAttribute("q", q);
        return "pharmacy-store";
    }

    @GetMapping("/pharmacy-store/{id}")
    public String medicineDetails(@PathVariable Long id, Authentication authentication, Model model) {
        addCurrentUser(authentication, model);
        model.addAttribute("medicine", medicineService.getMedicineById(id));
        return "medicine-detail";
    }

    // Place Order (form submission from the Medicine Details page)
    @PostMapping("/pharmacy-store/{id}/order")
    public String placeOrder(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(required = false) String deliveryAddress,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        User patient = currentUserService.get(authentication);
        orderService.placeOrder(patient, new PlaceOrderRequest(id, quantity, deliveryAddress));
        redirectAttributes.addFlashAttribute("orderPlaced", true);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderPage(Authentication authentication, Model model) {
        User patient = addCurrentUser(authentication, model);
        model.addAttribute("orders", orderService.getOrdersForPatient(patient.getId()));
        return "orders";
    }

    private User addCurrentUser(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        return user;
    }
}
