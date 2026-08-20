package com.healthcare.platform.service;

import com.healthcare.platform.model.Payment;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Online Payment feature (feature #14). Records sandbox payments made through the
 * bKash / Bank-Card checkout. No real gateway is contacted - every payment is
 * marked SUCCESS with a generated transaction id.
 */
@Service
public class PaymentService {

    private final PaymentRepository payments;

    public PaymentService(PaymentRepository payments) {
        this.payments = payments;
    }

    /** Records a successful sandbox payment and returns the saved row (with its transaction id). */
    public Payment record(User payer, BigDecimal amount, String method, String purpose,
                          String referenceType, Long referenceId, String rawAccount) {
        String txn = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment = new Payment(payer, amount, method, purpose, referenceType,
                referenceId, maskAccount(rawAccount), txn, "SUCCESS");
        return payments.save(payment);
    }

    public List<Payment> getForUser(Long userId) {
        return payments.findByPayerIdOrderByCreatedAtDesc(userId);
    }

    /** Every payment on the platform, newest first - for the admin audit view. */
    @Transactional(readOnly = true)
    public List<Payment> getAll() {
        return payments.findAllByOrderByCreatedAtDesc();
    }

    public Payment getById(Long id) {
        return payments.findById(id).orElse(null);
    }

    /** Masks all but the first 3 and last 3 characters of a mobile/card number. */
    static String maskAccount(String raw) {
        if (raw == null) {
            return null;
        }
        String digits = raw.replaceAll("\\s+", "");
        if (digits.isEmpty()) {
            return null;
        }
        if (digits.length() <= 6) {
            return digits.charAt(0) + "****";
        }
        String head = digits.substring(0, 3);
        String tail = digits.substring(digits.length() - 3);
        return head + "*".repeat(digits.length() - 6) + tail;
    }
}
