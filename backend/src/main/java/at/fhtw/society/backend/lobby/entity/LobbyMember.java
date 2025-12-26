package at.fhtw.society.backend.lobby.entity;

import at.fhtw.society.backend.session.dto.AvatarId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "lobby_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_lobby_member_lobby_player", columnNames = {"lobby_id", "player_id"}),
                @UniqueConstraint(name = "uq_lobby_member_player", columnNames = {"player_id"})
        }
)
public class LobbyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lobby_id", nullable = false)
    private Lobby lobby;

    // Player ID comes from JWT "sub" claim. We do not need a Player entity here.
    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(nullable = false, length = 40)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "avatar_id", nullable = false)
    private AvatarId avatarId;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LobbyRole role;

    @Column(nullable = false)
    private boolean ready;

    @PrePersist
    void prePersist() {
        if (joinedAt == null) {
            joinedAt = Instant.now();
        }
    }
}
