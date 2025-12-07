package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.ai.Message;
import at.fhtw.society.backend.game.dto.GameStatus;
import at.fhtw.society.backend.game.dto.CreateGameDto;
import at.fhtw.society.backend.player.entity.Gamemaster;
import at.fhtw.society.backend.player.entity.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "gamemaster_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_game_gamemaster")
    )
    private Gamemaster gamemaster;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "theme_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "fk_game_theme")
    )
    private Theme theme;

    @Column(name = "maxrounds", nullable = false)
    private Integer maxrounds;

    @Column(name = "maxplayers", nullable = false)
    private Integer maxplayers;

    @Column(name = "started_at", nullable = true)
    private OffsetDateTime startedAt;

    @Column(name = "ended_at", nullable = true)
    private OffsetDateTime endedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conversation", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> conversation = new HashMap<>();

    public Game(Gamemaster gamemaster, Theme theme, Integer maxRounds, Integer maxPlayers) {
        this.gamemaster = gamemaster;
        this.theme = theme;
        this.maxrounds = maxRounds;
        this.maxplayers = maxPlayers;
        this.status = GameStatus.CREATED;
    }

    @PrePersist
    void prePersist() {
        if (status == null) status = GameStatus.CREATED;
    }

    @ManyToMany(mappedBy = "games")
    private Set<Player> players;


    // add this field somewhere in the class (e.g., near other fields)
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public List<Message> getConversationList() {
        if (conversation == null) return List.of();

        Object raw = conversation.get("messages");
        if (raw == null) return List.of();

        // Safely convert List<Map<...>> → List<Message>
        List<Message> messages = OBJECT_MAPPER.convertValue(
                raw, new TypeReference<List<Message>>() {}
        );
        return messages != null ? messages : List.of();
    }

    public void setConversationList(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            // store an empty messages array to keep JSON shape predictable
            this.conversation = new HashMap<>(Map.of("messages", List.of()));
        } else {
            // assign a NEW map instance so Hibernate recognizes the mutation
            this.conversation = new HashMap<>(Map.of("messages", messages));
        }
    }
}
