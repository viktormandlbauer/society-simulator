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
                .defaultHeader("Authorization", "Bearer " + Objects.requireNonNull(props.getKey(), "API key is required"))
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.model = Objects.requireNonNull(props.getModel(), "API model definition is required");
    }

    public record Message(String role, String content) {
        public static Message system(String c)    { return new Message("system", c); }
        public static Message user(String c)      { return new Message("user", c); }
        public static Message assistant(String c) { return new Message("assistant", c); }
    }

    public List<Message> initConversation(Game game) {
        String system_context = "You are the AI Game Master for a turn-based social game.\n" +
                "Follow the rules strictly and keep responses concise, actionable, and on-theme.\n\n" +
                "Theme: " + game.getTheme() + '\n' +
                "Max rounds: " + String.valueOf(game.getMaxrounds()) + '\n';

        return chatReturnConversation(this.model, new ArrayList<>(List.of(Message.system(system_context))), 0.7, 512);
    }

    /** Convenience: start a convo with a single user message and get back the conversation (user + assistant). */
    public List<Message> chatReturnConversation(String userMessage) {
        return chatReturnConversation(this.model, new ArrayList<>(List.of(Message.user(userMessage))), 0.7, 512);
    }

    /** Core: send history, append assistant reply, and return the UPDATED conversation list. */
    @SuppressWarnings("unchecked")
    public List<Message> chatReturnConversation(String model,
                                                List<Message> history,
                                                double temperature,
                                                int maxTokens) {

        var req = Map.of(
                "model", model,
                "messages", history,            // send the whole conversation so far
                "temperature", temperature,
                "max_tokens", maxTokens,
                "stream", false
        );

        var body = (Map<String, Object>) http.post()
                .uri("/chat/completions")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Map.class)
                .block(Duration.ofSeconds(60));

        if (body == null) throw new IllegalStateException("DeepInfra call failed: null body");

        var choices = (List<Map<String, Object>>) body.get("choices");
        if (choices == null || choices.isEmpty())
            throw new IllegalStateException("DeepInfra call failed: no choices in response");

        var msgMap = (Map<String, Object>) choices.get(0).get("message");
        if (msgMap == null)
            throw new IllegalStateException("DeepInfra call failed: missing message object");

        var role = String.valueOf(msgMap.getOrDefault("role", "assistant"));
        var content = String.valueOf(msgMap.getOrDefault("content", ""));

        // Append assistant reply to the provided history and return it
        var updated = new ArrayList<>(history);
        updated.add(new Message(role, content));
        return updated;
    }
}
