package at.fhtw.society.backend.game.mappers;

import at.fhtw.society.backend.game.dto.GameDto;
import at.fhtw.society.backend.game.entity.Game;
import at.fhtw.society.backend.game.entity.Round;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface GameMapper {

    // Note: gamemaster might be null for guest sessions
    @Mapping(target = "gamemasterId", expression = "java(game.getLobby() != null && game.getLobby().getGamemaster() != null ? game.getLobby().getGamemaster().getId() : null)")
    @Mapping(target = "gamemasterUsername", expression = "java(game.getLobby() != null && game.getLobby().getGamemaster() != null ? game.getLobby().getGamemaster().getUsername() : null)")

    @Mapping(target = "themeId", source = "lobby.theme.id")
    @Mapping(target = "themeName", source = "lobby.theme.theme")

    @Mapping(target = "status", expression = "java(game.getStatus() != null ? game.getStatus().name() : null)")

    @Mapping(target = "currentPlayerCount",
            expression = "java(game.getLobby() != null && game.getLobby().getMembers() != null ? game.getLobby().getMembers().size() : 0)")
    @Mapping(target = "maxPlayerCount", source = "lobby.maxPlayers")

    @Mapping(target = "currentRound", source = "currentRound", qualifiedByName = "roundNumberOrZero")
    @Mapping(target = "maxRounds", source = "lobby.maxRounds")
    GameDto toDto(Game game);

    // Mapping DTO -> entity needs repositories (load Player/Theme by id), so keep it out of MapStruct.
    default Game toGame(GameDto dto) {
        throw new UnsupportedOperationException("GameDto -> Game mapping must be done in a service (needs DB lookups).");
    }

    @Named("roundNumberOrZero")
    static int roundNumberOrZero(Round round) {
        return (round == null || round.getNumber() == null) ? 0 : round.getNumber();
    }
}
