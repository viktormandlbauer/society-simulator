package at.fhtw.society.backend.lobby.entity;

import at.fhtw.society.backend.game.entity.Game;
import at.fhtw.society.backend.game.entity.Player;
import at.fhtw.society.backend.game.entity.Theme;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lobby")
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 60)
    private String name;

    /**
     * Moved from Game -> Lobby.
     * If you want to allow "no GM yet", make nullable = true / optional = true for a while.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gamemaster_id", nullable = false)
    private Player gamemaster;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private int maxRounds;

    @Column(length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private LobbyStatus status = LobbyStatus.OPEN;

    /**
     * Members are owned by the lobby. When a lobby is deleted, its members are also deleted.
     */
    @Builder.Default
    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LobbyMember> members = new ArrayList<>();

    /**
     * Optional back reference (1 lobby -> 1 game).
     */
    @OneToOne(mappedBy = "lobby", fetch = FetchType.LAZY)
    private Game game;

    @Transient
    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.isBlank();
    }
}
