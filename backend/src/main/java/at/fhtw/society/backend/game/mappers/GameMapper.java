package at.fhtw.society.backend.game.mappers;

import at.fhtw.society.backend.game.dto.GameDto;
import at.fhtw.society.backend.game.entity.Game;
import at.fhtw.society.backend.game.entity.Theme;
import at.fhtw.society.backend.player.entity.Gamemaster;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(target = "themeName", source = "theme.theme")
    @Mapping(target = "themeId", source = "theme.id")
    @Mapping(target = "gamemasterUsername", source = "gamemaster.username")
    @Mapping(target = "gamemasterId", source = "gamemaster.id")
    GameDto toDto(Game game);

    @Mapping(target = "theme", source = "themeId")
    @Mapping(target = "gamemaster", source = "gamemasterId")
    Game toGame(GameDto dto);

    default Theme mapThemeId(UUID id) {
        if (id == null) return null;
        Theme t = new Theme();
        t.setId(id);
        return t;
    }

    default Gamemaster mapGamemasterId(UUID id) {
        if (id == null) return null;
        Gamemaster g = new Gamemaster();
        g.setId(id);
        return g;
    }
}
