package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.game.GameStatus;
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

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    @Column(name = "theme", nullable = false, length = 256)
    private String theme;

    @Column(name = "maxrounds", nullable = false)
    private Integer maxrounds;

    @Column(name = "maxplayers", nullable = false)
    private Integer maxplayers;

    @Column(name = "started_at", nullable = true)
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
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

    public Game(CreateGameDto createGameDto, Gamemaster gamemaster) {
        this.gamemaster = gamemaster;
        this.theme = createGameDto.getTheme();
        this.maxrounds = createGameDto.getMaxRounds();
        this.maxplayers = createGameDto.getPlayerCount();
        this.status = GameStatus.CREATED;
    }

    @PrePersist
    void prePersist() {
        if (status == null) status = GameStatus.CREATED;
    }

    @ManyToMany(mappedBy = "games")
    private Set<Player> players;
}
