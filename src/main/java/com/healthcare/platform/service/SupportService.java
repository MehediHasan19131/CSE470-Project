package com.healthcare.platform.service;

import com.healthcare.platform.model.SupportMessage;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.SupportMessageRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contact-us / Report-a-problem messages. Anyone logged in can submit; admins
 * review and resolve them.
 */
@Service
public class SupportService {

    private final SupportMessageRepository messages;

    public SupportService(SupportMessageRepository messages) {
        this.messages = messages;
    }

    public SupportMessage submit(User sender, String type, String subject, String message) {
        String normalizedType = "REPORT".equalsIgnoreCase(type) ? "REPORT" : "CONTACT";
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Please write a message.");
        }
        String finalSubject = (subject == null || subject.isBlank())
                ? (normalizedType.equals("REPORT") ? "Problem report" : "Contact message")
                : subject.trim();
        return messages.save(new SupportMessage(sender, normalizedType, finalSubject, message.trim()));
    }

    @Transactional(readOnly = true)
    public List<SupportMessage> listAll() {
        return messages.findAllByOrderByCreatedAtDesc();
    }

    public long countOpen() {
        return messages.findAllByOrderByCreatedAtDesc().stream()
                .filter(m -> "OPEN".equals(m.getStatus()))
                .count();
    }

    public void resolve(Long id) {
        SupportMessage message = messages.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found."));
        message.setStatus("RESOLVED");
        messages.save(message);
    }
}
