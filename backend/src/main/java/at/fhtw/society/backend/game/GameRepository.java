package at.fhtw.society.backend.game;

import at.fhtw.society.backend.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Game findById(UUID gameId);
}
