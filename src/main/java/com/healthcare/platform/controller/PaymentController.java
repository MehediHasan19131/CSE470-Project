package com.healthcare.platform.controller;

import com.healthcare.platform.dto.DonationRequest;
import com.healthcare.platform.dto.PlaceOrderRequest;
import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.Payment;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.service.AmbulanceService;
import com.healthcare.platform.service.CampaignService;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.DonationService;
import com.healthcare.platform.service.MedicineService;
import com.healthcare.platform.service.OrderService;
import com.healthcare.platform.service.PaymentService;
import com.healthcare.platform.service.ServiceBookingService;
import java.math.BigDecimal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Online Payment feature (feature #14). One bKash / Bank-Card checkout that
 * handles several "intents": a campaign donation, a medicine order, a hospital
 * service / diagnostic test booking, or an ambulance fare. On success it records
 * a Payment and performs the matching domain action (place the order, record the
 * booking, mark the fare paid, record the donation). Sandbox only — no real gateway.
 */
@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final DonationService donationService;
    private final CampaignService campaignService;
    private final CurrentUserService currentUserService;
    private final MedicineService medicineService;
    private final OrderService orderService;
    private final ServiceBookingService serviceBookingService;
    private final AmbulanceService ambulanceService;

    public PaymentController(PaymentService paymentService, DonationService donationService,
                             CampaignService campaignService, CurrentUserService currentUserService,
                             MedicineService medicineService, OrderService orderService,
                             ServiceBookingService serviceBookingService, AmbulanceService ambulanceService) {
        this.paymentService = paymentService;
        this.donationService = donationService;
        this.campaignService = campaignService;
        this.currentUserService = currentUserService;
        this.medicineService = medicineService;
        this.orderService = orderService;
        this.serviceBookingService = serviceBookingService;
        this.ambulanceService = ambulanceService;
    }

    // Checkout page. The "intent" + item params arrive from wherever the user pressed "Pay".
    @GetMapping("/payment")
    public String checkout(@RequestParam(required = false) String intent,
                           @RequestParam(required = false) BigDecimal amount,
                           @RequestParam(required = false) Long campaignId,
                           @RequestParam(required = false) String message,
                           @RequestParam(required = false) String purpose,
                           @RequestParam(required = false) Long medicineId,
                           @RequestParam(required = false) Integer quantity,
                           @RequestParam(required = false) String deliveryAddress,
                           @RequestParam(required = false) Long providerId,
                           @RequestParam(required = false) String itemName,
                           @RequestParam(required = false) Long requestId,
                           Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));

        String normalizedIntent = normalize(intent);
        BigDecimal resolvedAmount = amount;
        String purposeLabel = purpose;

        if ("medicine".equals(normalizedIntent) && medicineId != null) {
            Medicine medicine = medicineService.getMedicineById(medicineId);
            int qty = (quantity != null && quantity > 0) ? quantity : 1;
            resolvedAmount = medicine.getPrice().multiply(BigDecimal.valueOf(qty));
            purposeLabel = "Medicine: " + medicine.getName() + " ×" + qty;
        } else if ("ambulance".equals(normalizedIntent)) {
            purposeLabel = "Ambulance fare";
        } else if ("service".equals(normalizedIntent)) {
            if (purposeLabel == null || purposeLabel.isBlank()) {
                purposeLabel = (itemName != null && !itemName.isBlank()) ? itemName : "Service booking";
            }
        } else if (campaignId != null) {
            normalizedIntent = "donation";
            try {
                purposeLabel = "Donation — " + campaignService.getCampaignById(campaignId).title();
            } catch (Exception ignored) {
                // campaign not found; keep whatever purpose we had
            }
        }

        if (resolvedAmount == null) {
            resolvedAmount = new BigDecimal("500");
        }
        if (purposeLabel == null || purposeLabel.isBlank()) {
            purposeLabel = "Payment";
        }

        model.addAttribute("intent", normalizedIntent.isBlank() ? null : normalizedIntent);
        model.addAttribute("amount", resolvedAmount);
        model.addAttribute("campaignId", campaignId);
        model.addAttribute("message", message);
        model.addAttribute("purposeLabel", purposeLabel);
        model.addAttribute("medicineId", medicineId);
        model.addAttribute("quantity", quantity);
        model.addAttribute("deliveryAddress", deliveryAddress);
        model.addAttribute("providerId", providerId);
        model.addAttribute("itemName", itemName);
        model.addAttribute("requestId", requestId);
        return "payment";
    }

    @PostMapping("/payment/process")
    public String process(@RequestParam(required = false) String intent,
                          @RequestParam BigDecimal amount,
                          @RequestParam String method,
                          @RequestParam String accountNumber,
                          @RequestParam(required = false) String pin,
                          @RequestParam(required = false) Long campaignId,
                          @RequestParam(required = false) String message,
                          @RequestParam(required = false) String purpose,
                          @RequestParam(required = false) Long medicineId,
                          @RequestParam(required = false) Integer quantity,
                          @RequestParam(required = false) String deliveryAddress,
                          @RequestParam(required = false) Long providerId,
                          @RequestParam(required = false) String itemName,
                          @RequestParam(required = false) Long requestId,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        User user = currentUserService.get(authentication);

        if (accountNumber == null || accountNumber.replaceAll("\\s+", "").length() < 6
                || amount == null || amount.signum() <= 0) {
            redirectAttributes.addFlashAttribute("error", "Enter a valid amount and account / mobile number.");
            return "redirect:/payments";
        }

        String normalizedIntent = normalize(intent);
        String purposeLabel = purpose;
        String referenceType = "GENERAL";
        Long referenceId = campaignId;

        try {
            switch (normalizedIntent) {
                case "medicine" -> {
                    int qty = (quantity != null && quantity > 0) ? quantity : 1;
                    orderService.placeOrder(user, new PlaceOrderRequest(medicineId, qty, deliveryAddress));
                    referenceType = "ORDER";
                    referenceId = medicineId;
                    if (purposeLabel == null || purposeLabel.isBlank()) {
                        purposeLabel = "Medicine order";
                    }
                }
                case "service" -> {
                    if (providerId != null) {
                        serviceBookingService.book(user, providerId, itemName != null ? itemName : "Service", amount);
                    }
                    referenceType = "SERVICE";
                    referenceId = providerId;
                    if (purposeLabel == null || purposeLabel.isBlank()) {
                        purposeLabel = (itemName != null && !itemName.isBlank()) ? itemName : "Service booking";
                    }
                }
                case "ambulance" -> {
                    if (requestId != null) {
                        ambulanceService.markFarePaid(user, requestId);
                    }
                    referenceType = "AMBULANCE";
                    referenceId = requestId;
                    purposeLabel = "Ambulance fare";
                }
                default -> {
                    if (campaignId != null) {
                        donationService.donate(user, campaignId, new DonationRequest(amount, message, method));
                        purposeLabel = "Donation — " + campaignService.getCampaignById(campaignId).title();
                        referenceType = "DONATION";
                        referenceId = campaignId;
                    }
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Sorry, we couldn't complete that payment. Please try again.");
            return "redirect:/payments";
        }

        if (purposeLabel == null || purposeLabel.isBlank()) {
            purposeLabel = "Payment";
        }

        Payment payment = paymentService.record(user, amount, method, purposeLabel,
                referenceType, referenceId, accountNumber);
        return "redirect:/payment/" + payment.getId() + "/receipt";
    }

    @GetMapping("/payment/{id}/receipt")
    public String receipt(@PathVariable Long id, Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        Payment payment = paymentService.getById(id);
        if (payment == null || payment.getPayer() == null
                || (!payment.getPayer().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN)) {
            return "redirect:/payments";
        }
        model.addAttribute("user", user);
        model.addAttribute("payment", payment);
        return "payment-success";
    }

    @GetMapping("/payments")
    public String history(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        boolean audit = user.getRole() == UserRole.ADMIN;
        model.addAttribute("auditMode", audit);
        model.addAttribute("payments", audit ? paymentService.getAll() : paymentService.getForUser(user.getId()));
        return "payment-history";
    }

    private String normalize(String intent) {
        return intent == null ? "" : intent.trim().toLowerCase();
    }
}
