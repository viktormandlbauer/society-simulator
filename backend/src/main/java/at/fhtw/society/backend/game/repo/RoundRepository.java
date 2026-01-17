package at.fhtw.society.backend.game.repo;

import at.fhtw.society.backend.game.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoundRepository extends JpaRepository<Round, UUID> {
    Optional<Round> findByGame_IdAndNumber(UUID gameId, Integer number);
    List<Round> findAllByGame_IdOrderByNumberAsc(UUID gameId);
}
