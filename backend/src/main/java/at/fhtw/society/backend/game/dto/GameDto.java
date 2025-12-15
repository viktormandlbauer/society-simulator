package at.fhtw.society.backend.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class GameDto {

    private UUID gamemasterId;
    private String gamemasterUsername;

    private UUID themeId;
    private String themeName;

    private String status;
    private int currentPlayerCount;
    private int maxPlayerCount;
    private int currentRound;
    private int maxRounds;


}