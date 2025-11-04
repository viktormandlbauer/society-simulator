package at.fhtw.society.backend.ai;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
public class DeepinfraService {

    private final WebClient http;
    private final String model;

    public DeepinfraService(DeepinfraProperties props) {
        this.http = httpClient(Objects.requireNonNull(props.getKey(), "API key is required"), Objects.requireNonNull(props.getUrl(), "API url is required"));
        this.model = Objects.requireNonNull(props.getModel(), "API model definition is required");
    }

    private WebClient httpClient(String apiKey, String url) {
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeader("Authorization", "Bearer " + Objects.requireNonNull(apiKey, "apiKey"))
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public record Message(String role, String content) {
        public static Message user(String content)    { return new Message("user", content); }
        public static Message system(String content)  { return new Message("system", content); }
        public static Message assistant(String content){ return new Message("assistant", content); }
    }

    // === Minimal one-liner convenience ===
    public String chat(String userMessage) {
        return chat(this.model, java.util.List.of(Message.user(userMessage)), 0.3, 200);
    }

    // === Basic API call (blocking) ===
    public String chat(String model,
                       java.util.List<Message> messages,
                       double temperature,
                       int maxTokens) {

        var req = java.util.Map.of(
                "model", model,
                "messages", messages,
                "temperature", temperature,
                "max_tokens", maxTokens,
                "stream", false
        );

        var resp = http.post()
                .uri("/chat/completions")
                .bodyValue(req)
                .retrieve()
                .toEntity(java.util.Map.class)
                .block(java.time.Duration.ofSeconds(60));

        if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("DeepInfra call failed: " + (resp == null ? "no response" : resp.getStatusCode()));
        }

        var body = (java.util.Map<String, Object>) resp.getBody();
        var choices = (java.util.List<java.util.Map<String, Object>>) body.get("choices");
        if (choices == null || choices.isEmpty()) return "";

        var message = (java.util.Map<String, Object>) choices.get(0).get("message");
        return message == null ? "" : String.valueOf(message.getOrDefault("content", ""));
    }

}