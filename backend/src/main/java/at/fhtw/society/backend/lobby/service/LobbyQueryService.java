package at.fhtw.society.backend.lobby.service;

import at.fhtw.society.backend.lobby.dto.LobbyListItemDto;
import at.fhtw.society.backend.lobby.entity.Lobby;
import at.fhtw.society.backend.lobby.mapper.LobbyListItemMapper;
import at.fhtw.society.backend.lobby.repo.LobbyListItemRow;
import at.fhtw.society.backend.lobby.repo.LobbyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LobbyQueryService {

    private final LobbyRepository lobbyRepository;
    private final LobbyListItemMapper lobbyListItemMapper;

    public LobbyQueryService(LobbyRepository lobbyRepository, LobbyListItemMapper lobbyListItemMapper) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyListItemMapper = lobbyListItemMapper;
    }

    @Transactional(readOnly = true)
    public List<LobbyListItemDto> getLobbyList() {
        return lobbyListItemMapper.toDtos(lobbyRepository.findLobbyList());
    }

}
