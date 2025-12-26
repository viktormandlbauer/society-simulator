package at.fhtw.society.backend.lobby.service;

import at.fhtw.society.backend.lobby.entity.LobbyMember;
import at.fhtw.society.backend.lobby.entity.LobbyRole;
import at.fhtw.society.backend.lobby.exception.LobbyMemberNotFoundException;
import at.fhtw.society.backend.lobby.exception.LobbyNotFoundException;
import at.fhtw.society.backend.lobby.repo.LobbyMemberRepository;
import at.fhtw.society.backend.lobby.repo.LobbyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class LobbyCommandService {

    private final LobbyRepository lobbyRepository;
    private final LobbyMemberRepository lobbyMemberRepository;

    public LobbyCommandService(LobbyRepository lobbyRepository, LobbyMemberRepository lobbyMemberRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyMemberRepository = lobbyMemberRepository;
    }

    /**
     * Player leaves the lobby. If the player is the gamemaster, a new gamemaster is assigned.
     * If the lobby becomes empty, it is deleted.
     * @param lobbyId - ID of the lobby to leave
     * @param playerId - ID of the player leaving the lobby
     */
    @Transactional
    public void leaveLobby(UUID lobbyId, UUID playerId) {
        if (!lobbyRepository.existsById(lobbyId)) throw new LobbyNotFoundException(lobbyId);

        LobbyMember lobbyMember = lobbyMemberRepository.findByLobby_IdAndPlayerId(lobbyId, playerId)
                .orElseThrow(() -> new LobbyMemberNotFoundException(lobbyId, playerId));

        // true if the leaving member is the game master
        boolean wasGamemaster = lobbyMember.getRole() == LobbyRole.GAMEMASTER;

        lobbyMemberRepository.delete(lobbyMember);

        boolean isLobbyEmpty = lobbyMemberRepository.countByLobby_Id(lobbyId) == 0L;

        if (isLobbyEmpty) {
            lobbyRepository.deleteById(lobbyId);
        } else if (wasGamemaster) {
            Optional<LobbyMember> newGamemaster = lobbyMemberRepository.findFirstByLobby_IdOrderByJoinedAtAsc(lobbyId);
            newGamemaster.ifPresent(member -> {
                member.setRole(LobbyRole.GAMEMASTER);
                lobbyMemberRepository.save(member);
            });
        }
    }
}
