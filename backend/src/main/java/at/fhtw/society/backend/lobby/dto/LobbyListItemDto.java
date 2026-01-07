package at.fhtw.society.backend.lobby.dto;

import at.fhtw.society.backend.lobby.entity.LobbyStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Response DTO for rendering the Lobby List screen.
 * Contains only the necessary information to display a list of lobbies.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LobbyListItemDto {

    private final UUID lobbyId;
    private final String name;

    private final UUID themeId;
    private final String themeName;

    private final int playersCount;
    private final int maxPlayers;

    private final boolean hasPassword;
    private final LobbyStatus status;
}
