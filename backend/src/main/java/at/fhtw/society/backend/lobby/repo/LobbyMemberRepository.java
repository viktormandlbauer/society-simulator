package at.fhtw.society.backend.lobby.repo;

import at.fhtw.society.backend.lobby.entity.LobbyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LobbyMemberRepository extends JpaRepository<LobbyMember, UUID> {

    long countByLobbyId(UUID lobbyId);

    Optional<LobbyMember> findByLobbyIdAndPlayerId(UUID lobbyId, UUID playerId);

    boolean existsByLobbyIdAndPlayerId(UUID lobbyId, UUID playerId);

    List<LobbyMember> findByLobbyIdOrderByJoinedAtAsc(UUID lobbyId);
}
