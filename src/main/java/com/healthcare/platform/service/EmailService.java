package com.healthcare.platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Notifications Module (Sprint 3) - Imtiaz Zaman Sami (23101551)
 * Email Notifications.
 * <p>
 * Sending real email requires SMTP credentials most student setups won't have
 * configured, so this service is guarded by {@code app.mail.enabled}
 * (application.properties, default false). When disabled, or if the send
 * itself fails for any reason (bad/missing SMTP config), it logs instead of
 * throwing - so a broken/unconfigured mail server never crashes a request.
 */
@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final String fromAddress;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.enabled:false}") boolean mailEnabled,
            @Value("${app.mail.from:no-reply@smartcare.local}") String fromAddress
    ) {
        this.mailSender = mailSender;
        this.mailEnabled = mailEnabled;
        this.fromAddress = fromAddress;
    }

    public void sendEmail(String to, String subject, String body) {
        if (!mailEnabled) {
            log.info("[Email disabled] Would send to {} | subject: {}", to, subject);
            return;
        }
        if (to == null || to.isBlank()) {
            log.warn("Skipping email send - recipient address is missing (subject: {})", subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {} | subject: {}", to, subject);
        } catch (Exception e) {
            // Never let a mail failure break the calling request/job.
            log.warn("Failed to send email to {} (subject: {}): {}", to, subject, e.getMessage());
        }
    }
}

