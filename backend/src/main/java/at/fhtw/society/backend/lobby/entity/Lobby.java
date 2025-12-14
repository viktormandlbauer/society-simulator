package at.fhtw.society.backend.lobby.entity;

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
@Table(name = "lobbies")
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 60)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private int maxRounds;

    @Column(length = 20)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private LobbyStatus status = LobbyStatus.OPEN;

    /**
     * Members are owned by the lobby. When a lobby is deleted, its members are also deleted.
     */
    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LobbyMember> members = new ArrayList<>();

    @Transient
    public boolean isHasPassword() {
        return passwordHash != null && !passwordHash.isBlank();
    }

}
