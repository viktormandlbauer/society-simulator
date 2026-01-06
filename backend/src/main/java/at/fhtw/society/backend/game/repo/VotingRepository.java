package at.fhtw.society.backend.game.repo;

import at.fhtw.society.backend.game.entity.Voting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VotingRepository extends JpaRepository<Voting, UUID> {
    boolean existsByRound_IdAndPlayer_Id(UUID roundId, UUID playerId);
    long countByRound_Id(UUID roundId);
    List<Voting> findAllByRound_Id(UUID roundId);
    Optional<Voting> findByRound_IdAndPlayer_Id(UUID roundId, UUID playerId);
}
