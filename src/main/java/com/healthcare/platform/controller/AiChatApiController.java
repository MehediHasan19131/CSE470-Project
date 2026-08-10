package com.healthcare.platform.controller;

import com.healthcare.platform.dto.ChatMessageResponse;
import com.healthcare.platform.dto.ChatRequest;
import com.healthcare.platform.dto.ChatResponse;
import com.healthcare.platform.dto.ChatSessionSummary;
import com.healthcare.platform.dto.SymptomCheckRequest;
import com.healthcare.platform.dto.SymptomCheckResponse;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.AiChatService;
import com.healthcare.platform.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sprint 4 — Backend: AI Chat Interface + Symptom Checker (local Llama model via Ollama).
 */
@RestController
public class AiChatApiController {
    private final AiChatService aiChatService;
    private final CurrentUserService currentUserService;

    public AiChatApiController(AiChatService aiChatService, CurrentUserService currentUserService) {
        this.aiChatService = aiChatService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/api/ai/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest body, Authentication authentication) {
        User user = currentUserService.get(authentication);
        return aiChatService.chat(user, body.sessionId(), body.message());
    }

    @PostMapping("/api/ai/symptom-check")
    public SymptomCheckResponse symptomCheck(@Valid @RequestBody SymptomCheckRequest body, Authentication authentication) {
        User user = currentUserService.get(authentication);
        return aiChatService.symptomCheck(user, body.sessionId(), body.symptoms());
    }

    @GetMapping("/api/ai/sessions")
    public List<ChatSessionSummary> sessions(Authentication authentication) {
        return aiChatService.sessions(currentUserService.get(authentication));
    }

    @GetMapping("/api/ai/sessions/{sessionId}/messages")
    public List<ChatMessageResponse> sessionMessages(@PathVariable String sessionId, Authentication authentication) {
        return aiChatService.sessionMessages(currentUserService.get(authentication), sessionId);
    }

    @DeleteMapping("/api/ai/sessions/{sessionId}")
    public void deleteSession(@PathVariable String sessionId, Authentication authentication) {
        aiChatService.deleteSession(currentUserService.get(authentication), sessionId);
    }

    @DeleteMapping("/api/ai/sessions")
    public void deleteAllSessions(Authentication authentication) {
        aiChatService.deleteAllHistory(currentUserService.get(authentication));
    }
}
