package at.fhtw.society.backend.lobby.repo;

import at.fhtw.society.backend.lobby.entity.LobbyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LobbyMemberRepository extends JpaRepository<LobbyMember, UUID> {
    // get one lobby member by lobby id and player id
    Optional<LobbyMember> findByLobby_IdAndPlayerId(UUID lobbyId, UUID playerId);
    // to find the first joined member (future host) in case the host leaves
    Optional<LobbyMember> findFirstByLobby_IdOrderByJoinedAtAsc(UUID lobbyId);
    // get all members in a lobby by lobby id ordered by joined time ascending
    List<LobbyMember> findByLobby_IdOrderByJoinedAtAsc(UUID lobbyId);
    // get the number of members in a lobby by lobby id (to check if empty or full)
    long countByLobby_Id(UUID lobbyId);
    // to check if a player is already in a lobby
    boolean existsByPlayerId(UUID playerId);
    // to check if a player is in a specific lobby
    boolean existsByLobby_IdAndPlayerId(UUID lobbyId, UUID playerId);
}
