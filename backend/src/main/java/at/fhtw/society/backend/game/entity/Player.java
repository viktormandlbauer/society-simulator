package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.lobby.entity.LobbyMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "player")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Player belongs to lobbies via LobbyMember (your existing model).
     * If you don't need navigation from player -> memberships, you can remove this.
     */
    @OneToMany(mappedBy = "player")
    private Set<LobbyMember> lobbyMemberships = new HashSet<>();

    @OneToMany(mappedBy = "player")
    private Set<Voting> votes = new HashSet<>();
}
