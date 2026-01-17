package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.ai.Message;
import at.fhtw.society.backend.game.dto.GameStatus;
import at.fhtw.society.backend.lobby.entity.Lobby;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "game")
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private GameStatus status;

    /**
     * The ONE source of "setup truth":
     * theme, maxPlayers, maxRounds, members, gamemaster, etc.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "lobby_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_game_lobby")
    )
    private Lobby lobby;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "current_round_id",
            foreignKey = @ForeignKey(name = "fk_game_current_round")
    )
    private Round currentRound;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conversation", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> conversation = new HashMap<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Round> rounds = new HashSet<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Game(Lobby lobby) {
        this.lobby = lobby;
    }

    @PrePersist
    void prePersist() {
        if (status == null) status = GameStatus.CREATED;
        if (conversation == null) conversation = new HashMap<>(Map.of("messages", List.of()));
    }

    public List<Message> getConversationList() {
        if (conversation == null) return List.of();
        Object raw = conversation.get("messages");
        if (raw == null) return List.of();

        List<Message> messages = OBJECT_MAPPER.convertValue(raw, new TypeReference<List<Message>>() {});
        return messages != null ? messages : List.of();
    }

    public void setConversationList(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            this.conversation = new HashMap<>(Map.of("messages", List.of()));
        } else {
            this.conversation = new HashMap<>(Map.of("messages", messages));
        }
    }

    // Convenience (not persisted, but nice)
    // Note: gamemaster might be null for guest sessions, check LobbyMember with GAMEMASTER role instead
    @Transient public Player getGamemaster() { return lobby != null ? lobby.getGamemaster() : null; }
    @Transient public Theme getTheme() { return lobby != null ? lobby.getTheme() : null; }
    @Transient public int getMaxPlayers() { return lobby != null ? lobby.getMaxPlayers() : 0; }
    @Transient public int getMaxRounds() { return lobby != null ? lobby.getMaxRounds() : 0; }
}
