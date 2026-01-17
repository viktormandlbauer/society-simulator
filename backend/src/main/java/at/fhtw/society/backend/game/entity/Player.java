package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.lobby.entity.LobbyMember;
import at.fhtw.society.backend.session.dto.AvatarId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "player")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "avatar_id", nullable = false)
    private AvatarId avatarId;

    @Column(name = "username", unique = true, length = 64)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "is_guest", nullable = false)
    private boolean isGuest;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Note: LobbyMember stores playerId as UUID, not a Player entity reference.
     * So we cannot have a bidirectional relationship here.
     * If you need to find lobby memberships, query LobbyMemberRepository by playerId.
     */

    @OneToMany(mappedBy = "player")
    private Set<Voting> votes = new HashSet<>();

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
