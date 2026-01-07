package at.fhtw.society.backend.lobby.dto;

import at.fhtw.society.backend.lobby.entity.LobbyStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class LobbyViewDto {
    private final UUID lobbyId;
    private final String name;

    private final UUID themeId;
    private final String themeName;

    private final int maxPlayers;
    private final int maxRounds;

    private final boolean hasPassword;
    private final LobbyStatus status;

    private final List<LobbyMemberViewDto> members;
}
