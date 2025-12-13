package at.fhtw.society.backend.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class GuestSessionResponseDto {

    /**
     * The technical id of the player (UUID as string).
     */
    private final String playerId;

    /**
     * The (trimmed/normalized) name that will be used in the game.
     */
    private final String name;

    /**
     * The chosen avatar id (e.g. "orange").
     */
    private final AvatarId avatarId;

    /**
     * The signed JWT that represents this player identity.
     */
    private final String token;

    /**
     * The expiration time of the issued token.
     */
    private final Instant expiresAt;
}
