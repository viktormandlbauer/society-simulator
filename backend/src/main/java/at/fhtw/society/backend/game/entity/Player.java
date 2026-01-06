package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.game.entity.Game;
import at.fhtw.society.backend.game.entity.Voting;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

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

    @ManyToMany
    @JoinTable(
            name = "game_players",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "player_id"})
    )
    private Set<Game> games = new HashSet<>();

    // optional: useful if you want to navigate from player -> votes
    @OneToMany(mappedBy = "player")
    private Set<Voting> votes = new HashSet<>();
}
