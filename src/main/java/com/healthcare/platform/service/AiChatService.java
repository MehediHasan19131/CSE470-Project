package com.healthcare.platform.service;

import com.healthcare.platform.dto.ChatMessageResponse;
import com.healthcare.platform.dto.ChatResponse;
import com.healthcare.platform.dto.ChatSessionSummary;
import com.healthcare.platform.dto.DoctorRecommendation;
import com.healthcare.platform.dto.ServiceListingResponse;
import com.healthcare.platform.dto.SymptomCheckResponse;
import com.healthcare.platform.model.AiChatMessage;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.AiChatMessageRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Sprint 4 — AI Chat Interface + Symptom Checker, backed by a locally self-hosted
 * Llama-based medical model (see OllamaClient / sprint4/README.md).
 *
 * Important design choice: the LLM is only ever asked for free-text medical guidance and a
 * *specialty name*. Actual doctor recommendations ("find a good doctor based on reviews") are
 * never left to the model to invent — they come from a real query against our own doctors +
 * ratings data (ListingService/RatingRepository), sorted by real average rating. This avoids the
 * model hallucinating doctor names, ratings, or availability.
 */
@Service
public class AiChatService {
    private static final Pattern SPECIALTY_PATTERN = Pattern.compile("RECOMMENDED_SPECIALTY:\\s*(.+)", Pattern.CASE_INSENSITIVE);

    private static final String CHAT_SYSTEM_PROMPT = """
            You are the SmartCare AI Health Assistant, a general medical information helper embedded in a \
            healthcare platform. Answer health questions clearly, briefly, and cautiously. You are NOT a \
            doctor and must never claim to diagnose a condition. Always remind the user to consult a \
            licensed healthcare professional for real diagnosis or treatment. If the user describes symptoms \
            that could be a medical emergency (e.g. chest pain, trouble breathing, severe bleeding, stroke \
            signs, suicidal thoughts), tell them clearly to seek emergency care or call local emergency \
            services immediately, and mention this platform's Ambulance Booking feature.""";

    private static final String SYMPTOM_SYSTEM_PROMPT = """
            You are the SmartCare Symptom Checker. The user will describe symptoms. Respond with:
            1) A brief, cautious note on possible general causes (not a diagnosis).
            2) A clear urgency level (routine / see a doctor soon / urgent / emergency).
            3) Practical self-care advice if appropriate.
            You are NOT a doctor. Always recommend seeing a licensed professional for real diagnosis, and \
            urge immediate emergency care for severe or life-threatening symptoms.
            Finish your reply with exactly one line in this exact format so it can be parsed by the app:
            RECOMMENDED_SPECIALTY: <one specialty, e.g. Cardiology, Dermatology, General Medicine, \
            Pediatrics, Neurology, Orthopedics, Gastroenterology, ENT, Psychiatry, Gynecology>
            If nothing specific applies, use RECOMMENDED_SPECIALTY: General Medicine.""";

    private final AiChatMessageRepository messages;
    private final OllamaClient ollamaClient;
    private final ListingService listingService;

    public AiChatService(AiChatMessageRepository messages, OllamaClient ollamaClient, ListingService listingService) {
        this.messages = messages;
        this.ollamaClient = ollamaClient;
        this.listingService = listingService;
    }

    public ChatResponse chat(User user, String requestedSessionId, String userMessage) {
        String sessionId = sessionIdOrNew(requestedSessionId);
        List<AiChatMessage> history = messages.findByUserIdAndSessionIdOrderByCreatedAtAsc(user.getId(), sessionId);

        List<Map<String, String>> conversation = buildConversation(CHAT_SYSTEM_PROMPT, history, userMessage);
        String reply = callModelOrFail(conversation);

        messages.save(new AiChatMessage(user, sessionId, "user", userMessage, "chat"));
        messages.save(new AiChatMessage(user, sessionId, "assistant", reply, "chat"));

        return new ChatResponse(sessionId, reply, LocalDateTime.now());
    }

    public SymptomCheckResponse symptomCheck(User user, String requestedSessionId, String symptoms) {
        String sessionId = sessionIdOrNew(requestedSessionId);
        List<AiChatMessage> history = messages.findByUserIdAndSessionIdOrderByCreatedAtAsc(user.getId(), sessionId);

        List<Map<String, String>> conversation = buildConversation(SYMPTOM_SYSTEM_PROMPT, history, symptoms);
        String rawReply = callModelOrFail(conversation);

        String specialty = extractSpecialty(rawReply);
        String assessment = SPECIALTY_PATTERN.matcher(rawReply).replaceAll("").trim();

        messages.save(new AiChatMessage(user, sessionId, "user", symptoms, "symptom_check"));
        messages.save(new AiChatMessage(user, sessionId, "assistant", rawReply, "symptom_check"));

        List<DoctorRecommendation> recommendations = listingService.doctors(specialty, null).stream()
                .sorted(Comparator.comparing(ServiceListingResponse::averageRating).reversed())
                .limit(5)
                .map(DoctorRecommendation::from)
                .toList();

        return new SymptomCheckResponse(sessionId, assessment, specialty, recommendations, LocalDateTime.now());
    }

    public List<ChatSessionSummary> sessions(User user) {
        List<AiChatMessage> all = messages.findByUserIdOrderByCreatedAtAsc(user.getId());
        Map<String, ChatSessionSummary> bySession = new LinkedHashMap<>();
        for (AiChatMessage message : all) {
            String preview = message.getContent().length() > 80
                    ? message.getContent().substring(0, 80) + "..."
                    : message.getContent();
            bySession.put(message.getSessionId(), new ChatSessionSummary(
                    message.getSessionId(), preview, message.getMode(), message.getCreatedAt()));
        }
        return bySession.values().stream()
                .sorted(Comparator.comparing(ChatSessionSummary::lastMessageAt).reversed())
                .toList();
    }

    public List<ChatMessageResponse> sessionMessages(User user, String sessionId) {
        return messages.findByUserIdAndSessionIdOrderByCreatedAtAsc(user.getId(), sessionId).stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    public void deleteSession(User user, String sessionId) {
        messages.deleteByUserIdAndSessionId(user.getId(), sessionId);
    }

    public void deleteAllHistory(User user) {
        messages.deleteByUserId(user.getId());
    }

    private List<Map<String, String>> buildConversation(String systemPrompt, List<AiChatMessage> history, String newUserMessage) {
        List<Map<String, String>> conversation = new java.util.ArrayList<>();
        conversation.add(Map.of("role", "system", "content", systemPrompt));
        for (AiChatMessage past : history) {
            conversation.add(Map.of("role", past.getRole(), "content", past.getContent()));
        }
        conversation.add(Map.of("role", "user", "content", newUserMessage));
        return conversation;
    }

    private String callModelOrFail(List<Map<String, String>> conversation) {
        try {
            return ollamaClient.chat(conversation);
        } catch (AiUnavailableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    private String extractSpecialty(String reply) {
        Matcher matcher = SPECIALTY_PATTERN.matcher(reply);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "General Medicine";
    }

    private String sessionIdOrNew(String requested) {
        return (requested == null || requested.isBlank()) ? UUID.randomUUID().toString() : requested;
    }
}
