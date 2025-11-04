package at.fhtw.society.backend.game.model;
import at.fhtw.society.backend.player.entity.Player;
import jakarta.persistence.*;

@Entity
@Table(
        name = "game_players",
        uniqueConstraints = @UniqueConstraint(name = "uq_game_player", columnNames = {"game_id", "player_id"})
)
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // matches BIGSERIAL
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "game_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_gp_game")
    )
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "player_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_gp_player")
    )
    private Player player;

    public GamePlayer() {}

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    // Getters/setters
    public Long getId() { return id; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    // Equality by surrogate id
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GamePlayer gp)) return false;
        return id != null && id.equals(gp.id);
    }
    @Override public int hashCode() { return 31; }
}
