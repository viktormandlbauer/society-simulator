package at.fhtw.society.backend.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter @Getter
public class CreateGameDto {

    private UUID gamemasterId;
    private int playerCount;
    private String theme;
    private int maxRounds;
    private int maxTurnTime;

}