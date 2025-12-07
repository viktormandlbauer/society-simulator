package at.fhtw.society.backend.game.dto;

import at.fhtw.society.backend.game.entity.Theme;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter @Getter
public class CreateGameDto {
    private String username;
    private int playerCount;
    private String themeName;
    private int maxRounds;
    private int maxTurnTime;
}