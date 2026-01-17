package at.fhtw.society.backend.game.repo;

import at.fhtw.society.backend.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    Optional<Game> findById(UUID id);

}
