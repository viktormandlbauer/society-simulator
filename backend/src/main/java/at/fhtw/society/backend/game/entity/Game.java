package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.ai.Message;
import at.fhtw.society.backend.game.dto.GameStatus;
import at.fhtw.society.backend.game.entity.Player;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "gamemaster_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_game_gamemaster_player")
    )
    private Player gamemaster;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "theme_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_game_theme")
    )
    private Theme theme;

    @Column(name = "maxrounds", nullable = false)
    private Integer maxRounds;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "current_round_id",
            foreignKey = @ForeignKey(name = "fk_game_current_round")
    )
    private Round currentRound;

    @Column(name = "maxplayers", nullable = false)
    private Integer maxPlayers;

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

    @ManyToMany(mappedBy = "games")
    private Set<Player> players = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Round> rounds = new HashSet<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Game(Player gm, Theme theme, int maxRounds, int maxPlayers) {
        this.gamemaster = gm;
        this.theme = theme;
        this.maxRounds = maxRounds;
        this.maxPlayers = maxPlayers;
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
}
