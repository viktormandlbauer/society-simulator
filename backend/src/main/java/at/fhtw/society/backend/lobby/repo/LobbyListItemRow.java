package at.fhtw.society.backend.lobby.repo;

import at.fhtw.society.backend.lobby.entity.LobbyStatus;

import java.util.UUID;

/**
 * Projection interface for listing lobby items with selected fields.
 * This interface is used to retrieve specific columns from the lobby table
 * along with related theme information without loading entire entities.
 * It provides a lightweight way to fetch lobby details for display purposes.
 */
public interface LobbyListItemRow {
    UUID getLobbyId();
    String getName();
    UUID getThemeId();
    String getThemeName();
    // Postgres returns count as long(bigint) by default
    long getPlayersCount();
    int getMaxPlayers();
    boolean getHasPassword();
    LobbyStatus getStatus();
}
