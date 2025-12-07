package at.fhtw.society.backend.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GamePreviewDto {
    public String theme;
    public String status;
    public int currentPlayerCount;
    public int maxPlayerCount;
    public int currentRound;
    public int maxRounds;
    public String gamemaster;
}
