package at.fhtw.society.backend.game;

import at.fhtw.society.backend.game.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Integer> {
    List<Game> findAllByStatus(String status);
    List<Game> findAllByStatusOrderByCreatedAtDesc(String status);
    Page<Game> findByStatus(String status, Pageable pageable);
}
