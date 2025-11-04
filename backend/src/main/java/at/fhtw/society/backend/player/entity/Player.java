// java
package at.fhtw.society.backend.player.entity;

import at.fhtw.society.backend.game.model.GamePlayer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "player")
public class Player extends User {
    public void setId(Long id) { super.setId(id); } // enable manual PK set from service

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void addGamePlayer(GamePlayer gp) {
        gamePlayers.add(gp);
        gp.setPlayer(this);
    }

    public void removeGamePlayer(GamePlayer gp) {
        gamePlayers.remove(gp);
        gp.setPlayer(null);
    }
}