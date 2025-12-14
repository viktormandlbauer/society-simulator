package at.fhtw.society.backend.lobby.service;

import at.fhtw.society.backend.lobby.repo.LobbyMemberRepository;
import at.fhtw.society.backend.lobby.repo.LobbyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LobbyMembershipService {

    private final LobbyRepository lobbyRepository;
    private final LobbyMemberRepository lobbyMemberRepository;

    public LobbyMembershipService(LobbyRepository lobbyRepository, LobbyMemberRepository lobbyMemberRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyMemberRepository = lobbyMemberRepository;
    }

    // TODO: Error handling (lobby or member not found)
    @Transactional
    public void leaveLobby(UUID lobbyId, UUID playerID) {
        lobbyMemberRepository.findByLobbyIdAndPlayerId(lobbyId, playerID)
                .ifPresent(lobbyMemberRepository::delete);

        long remaining = lobbyMemberRepository.countByLobbyId(lobbyId);
        if (remaining == 0) {
            lobbyRepository.deleteById(lobbyId);
        }
    }

}
