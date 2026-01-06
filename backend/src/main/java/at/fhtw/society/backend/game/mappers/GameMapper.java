package at.fhtw.society.backend.game.mappers;

import at.fhtw.society.backend.game.dto.GameDto;
import at.fhtw.society.backend.game.entity.Game;
import at.fhtw.society.backend.game.entity.Round;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(target = "gamemasterId", source = "gamemaster.id")
    @Mapping(target = "gamemasterUsername", source = "gamemaster.username")

    @Mapping(target = "themeId", source = "theme.id")
    @Mapping(target = "themeName", source = "theme.theme")

    @Mapping(target = "status", expression = "java(game.getStatus() != null ? game.getStatus().name() : null)")

    @Mapping(target = "currentPlayerCount",
            expression = "java(game.getPlayers() != null ? game.getPlayers().size() : 0)")
    @Mapping(target = "maxPlayerCount", source = "maxPlayers")

    @Mapping(target = "currentRound", source = "currentRound", qualifiedByName = "roundNumberOrZero")
    @Mapping(target = "maxRounds", source = "maxRounds")
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
