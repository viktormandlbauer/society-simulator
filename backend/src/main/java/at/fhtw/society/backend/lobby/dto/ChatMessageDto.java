package at.fhtw.society.backend.lobby.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    private UUID playerId;
    private String playerName;
    private String avatarId;
    private String message;
    private Instant timestamp;
}
