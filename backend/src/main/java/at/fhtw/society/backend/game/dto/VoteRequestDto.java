package at.fhtw.society.backend.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class VoteRequestDto {
    private UUID playerId;
    private int choiceId;
}
