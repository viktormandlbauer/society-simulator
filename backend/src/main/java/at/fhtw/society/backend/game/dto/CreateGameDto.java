package at.fhtw.society.backend.game.dto;

import at.fhtw.society.backend.player.entity.GameMaster;

public class CreateGameDto {

    private GameMaster gamemaster;

    private int playerCount;
    private String theme;
    private int maxRounds;
    private int maxTurnTime;

    public GameMaster getGamemaster() {
        return gamemaster;
    }

    public void setGamemaster(GameMaster gamemaster) {
        this.gamemaster = gamemaster;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public int getMaxTurnTime() {
        return maxTurnTime;
    }

    public void setMaxTurnTime(int maxTurnTime) {
        this.maxTurnTime = maxTurnTime;
    }
}