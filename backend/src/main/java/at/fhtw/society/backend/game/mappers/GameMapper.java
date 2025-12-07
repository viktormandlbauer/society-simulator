package at.fhtw.society.backend.mappers;

import at.fhtw.society.backend.game.dto.GameDto;
import at.fhtw.society.backend.game.entity.Game;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel =  "spring")
public interface GameMapper {
    GameDto toDto(Game game);
    Game toGame(GameDto gameDto);
    List<GameDto> toDtos(List<Game> games);
    List<Game> toGames(List<GameDto> gameDtos);
}
