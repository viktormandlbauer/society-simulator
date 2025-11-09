package at.fhtw.society.backend.game.repo;

import at.fhtw.society.backend.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Game findById(UUID gameId);
}
