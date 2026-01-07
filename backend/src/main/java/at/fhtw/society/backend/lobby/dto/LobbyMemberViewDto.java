package at.fhtw.society.backend.lobby.dto;

import at.fhtw.society.backend.lobby.entity.LobbyRole;
import at.fhtw.society.backend.session.dto.AvatarId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class LobbyMemberViewDto {
    private final UUID playerId;
    private final String name;
    private final AvatarId avatarId;

    private final Instant joinedAt;
    private final LobbyRole role;
    private final boolean ready;
}
