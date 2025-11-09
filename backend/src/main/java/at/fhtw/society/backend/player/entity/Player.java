// java
package at.fhtw.society.backend.player.entity;

import at.fhtw.society.backend.game.entity.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "player")
@Getter @Setter @NoArgsConstructor
public class Player extends User {

    @ManyToMany
    @JoinTable
    (
        name = "game_players",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "game_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "player_id"})
    )
    private Set<Game> games = new HashSet<>();
}