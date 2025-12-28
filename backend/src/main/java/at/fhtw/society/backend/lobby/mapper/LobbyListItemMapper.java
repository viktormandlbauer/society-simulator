package at.fhtw.society.backend.lobby.mapper;

import at.fhtw.society.backend.common.config.MapStructCentralConfig;
import at.fhtw.society.backend.lobby.dto.LobbyListItemDto;
import at.fhtw.society.backend.lobby.repo.LobbyListItemRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructCentralConfig.class)
public interface LobbyListItemMapper {

    @Mapping(target = "playersCount", expression = "java(Math.toIntExact(row.getPlayersCount()))")
    LobbyListItemDto toDto(LobbyListItemRow row);

    List<LobbyListItemDto> toDtos(List<LobbyListItemRow> rows);
}
