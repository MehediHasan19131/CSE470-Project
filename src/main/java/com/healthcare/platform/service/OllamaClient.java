package com.healthcare.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sprint 4 — AI Chat / Symptom Checker.
 * Thin client for a locally self-hosted Llama-based medical model served by Ollama
 * (https://ollama.com), a free local model runner — no cloud API key, no new Maven dependency
 * (uses the JDK's built-in HttpClient + the Jackson ObjectMapper Spring Boot already provides).
 *
 * Recommended model: Llama3-OpenBioLLM-8B (Llama-3-8B fine-tuned on biomedical/clinical text).
 * See sprint4/README.md for the exact `ollama pull` / `ollama run` setup commands.
 */
@Service
public class OllamaClient {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String model;

    public OllamaClient(
            ObjectMapper objectMapper,
            @Value("${app.ai.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${app.ai.ollama.model:openbiollm}") String model
    ) {
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    /**
     * messages: ordered list of {"role": "system"|"user"|"assistant", "content": "..."}
     * Returns the assistant's reply text.
     */
    public String chat(List<Map<String, String>> messages) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", model);
            body.put("messages", messages);
            body.put("stream", false);
            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/chat"))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new AiUnavailableException(
                        "Ollama returned HTTP " + response.statusCode() + ". Response: " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode content = root.path("message").path("content");
            if (content.isMissingNode() || content.isNull()) {
                throw new AiUnavailableException("Unexpected response shape from Ollama: " + response.body());
            }
            return content.asText().trim();
        } catch (IOException e) {
            throw new AiUnavailableException(
                    "Could not reach the local Ollama server at " + baseUrl
                            + ". Run `ollama serve` and make sure the model \"" + model + "\" is pulled.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiUnavailableException("The request to the AI model was interrupted.", e);
        }
    }
}
