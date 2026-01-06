package at.fhtw.society.backend.ai;

import at.fhtw.society.backend.game.entity.Game;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;

@Service
public class DeepinfraService {

    private final WebClient http;
    private final String model;

    public DeepinfraService(DeepinfraProperties props) {
        this.http = WebClient.builder()
                .baseUrl(Objects.requireNonNull(props.getUrl(), "API url is required"))
                .defaultHeader("Authorization", "Bearer " + Objects.requireNonNull(props.getApiKey(), "API key is required"))
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.model = Objects.requireNonNull(props.getModel(), "API model definition is required");
    }

    /** Start a conversation with a system prompt and return the whole conversation (system + assistant). */
    public List<Message> initConversation(String systemContext) {
        List<Message> history = new ArrayList<>();
        history.add(new Message("system", systemContext));
        return chatReturnConversation(this.model, history, 0.7, 512);
    }

    /** Convenience: start a convo with a single user message and get back the conversation (user + assistant). */
    public List<Message> chatReturnConversation(String userMessage) {
        List<Message> history = new ArrayList<>();
        history.add(new Message("user", userMessage));
        return chatReturnConversation(this.model, history, 0.7, 512);
    }

    /** Core: send history, append assistant reply, and return the UPDATED conversation list. */
    @SuppressWarnings("unchecked")
    public List<Message> chatReturnConversation(String model,
                                                List<Message> history,
                                                double temperature,
                                                int maxTokens) {

        Map<String, Object> req = Map.of(
                "model", model,
                "messages", history,    // uses your Lombok POJO (role/content getters)
                "temperature", temperature,
                "max_tokens", maxTokens,
                "stream", false
        );

        Map<String, Object> body = (Map<String, Object>) http.post()
                .uri("/chat/completions")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Map.class)
                .block(Duration.ofSeconds(60));

        if (body == null) {
            throw new IllegalStateException("DeepInfra call failed: null body");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("DeepInfra call failed: no choices in response");
        }

        Map<String, Object> msgMap = (Map<String, Object>) choices.get(0).get("message");
        if (msgMap == null) {
            throw new IllegalStateException("DeepInfra call failed: missing message object");
        }

        String role = String.valueOf(msgMap.getOrDefault("role", "assistant"));
        String content = String.valueOf(msgMap.getOrDefault("content", ""));

        List<Message> updated = new ArrayList<>(history);
        updated.add(new Message(role, content));
        return updated;
    }

    /* ===========================
       chatConversion conveniences
       =========================== */

    /** Continue a chat given the full history (messages list). */
    public List<Message> chatConversion(List<Message> history) {
        return chatReturnConversation(this.model, new ArrayList<>(history), 0.7, 512);
    }

    /** Continue a chat using the conversation stored in a Game entity. */
    public List<Message> chatConversion(Game game) {
        List<Message> history = game.getConversationList(); // expects the entity helper you added
        List<Message> updated = chatReturnConversation(this.model, new ArrayList<>(history), 0.7, 512);

        // Persist back into the jsonb map
        if (game.getConversation() == null) {
            game.setConversation(new HashMap<>());
        }
        game.getConversation().put("messages", updated);

        return updated;
    }
}
