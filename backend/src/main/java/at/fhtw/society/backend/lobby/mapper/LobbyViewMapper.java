package at.fhtw.society.backend.lobby.mapper;

import at.fhtw.society.backend.common.config.MapStructCentralConfig;
import at.fhtw.society.backend.lobby.dto.LobbyMemberViewDto;
import at.fhtw.society.backend.lobby.dto.LobbyViewDto;
import at.fhtw.society.backend.lobby.entity.Lobby;
import at.fhtw.society.backend.lobby.entity.LobbyMember;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Comparator;

@Mapper(config = MapStructCentralConfig.class)
public interface LobbyViewMapper {

    @Mapping(target = "lobbyId", source = "id")
    @Mapping(target = "themeId", source = "theme.id")
    @Mapping(target = "themeName", source = "theme.theme")
    @Mapping(target = "hasPassword", expression = "java(lobby.hasPassword())")
    LobbyViewDto toDto (Lobby lobby);

    @Mapping(target = "playerId", source = "playerId")
    LobbyMemberViewDto toDto (LobbyMember lobbyMember);

    // Sort members by joinedAt after mapping
    @AfterMapping
    default void sortMember(@MappingTarget LobbyViewDto.LobbyViewDtoBuilder dtoBuilder, Lobby lobby) {
        lobby.getMembers().sort(Comparator.comparing(LobbyMember::getJoinedAt));
    }
}
