package at.fhtw.society.backend.game.model;

import at.fhtw.society.backend.game.GameStatus;
import at.fhtw.society.backend.game.dto.CreateGameDto;
import at.fhtw.society.backend.player.entity.GameMaster;
import at.fhtw.society.backend.player.entity.Player;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private GameStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "gamemaster_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_game_gamemaster")
    )
    private GameMaster gamemaster;

    @Column(name = "theme", nullable = false, length = 256)
    private String theme;

    @Column(name = "maxrounds", nullable = false)
    private Integer maxrounds;

    @Column(name = "maxplayers", nullable = false)
    private Integer maxplayers;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Game() {}

    public Game(CreateGameDto createGameDto) {
        this.gamemaster = createGameDto.getGamemaster();
        this.theme = createGameDto.getTheme();
        this.maxrounds = createGameDto.getMaxRounds();
        this.maxplayers = createGameDto.getPlayerCount();
        this.status = GameStatus.CREATED;
    }

    @PrePersist
    void prePersist() {
        if (status == null) status = GameStatus.CREATED;
    }

    // getters/setters
    public Long getId() { return id; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public GameMaster getGamemaster() { return gamemaster; }
    public void setGamemaster(GameMaster gamemaster) { this.gamemaster = gamemaster; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public Integer getMaxrounds() { return maxrounds; }
    public void setMaxrounds(Integer maxrounds) { this.maxrounds = maxrounds; }

    public Integer getMaxplayers() { return maxplayers; }
    public void setMaxplayers(Integer maxplayers) { this.maxplayers = maxplayers; }

    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }

    public OffsetDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<GamePlayer> gamePlayers = new java.util.HashSet<>();

    public void addPlayer(Player p) {
        var link = new GamePlayer(this, p);
        gamePlayers.add(link);
        p.getGamePlayers().add(link);
    }
    public void removePlayer(Player p) {
        gamePlayers.removeIf(link -> {
            boolean match = link.getPlayer().equals(p);
            if (match) p.getGamePlayers().remove(link);
            return match;
        });
    }
}
