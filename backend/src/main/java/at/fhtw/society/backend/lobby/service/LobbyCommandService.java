package at.fhtw.society.backend.lobby.service;

import at.fhtw.society.backend.game.entity.Theme;
import at.fhtw.society.backend.game.repo.ThemeRepository;
import at.fhtw.society.backend.lobby.dto.CreateLobbyRequestDto;
import at.fhtw.society.backend.lobby.dto.LobbyViewDto;
import at.fhtw.society.backend.lobby.entity.Lobby;
import at.fhtw.society.backend.lobby.entity.LobbyMember;
import at.fhtw.society.backend.lobby.entity.LobbyRole;
import at.fhtw.society.backend.lobby.entity.LobbyStatus;
import at.fhtw.society.backend.lobby.exception.LobbyMemberNotFoundException;
import at.fhtw.society.backend.lobby.exception.LobbyNotFoundException;
import at.fhtw.society.backend.lobby.exception.PlayerAlreadyInLobbyException;
import at.fhtw.society.backend.lobby.exception.ThemeNotFoundException;
import at.fhtw.society.backend.lobby.mapper.LobbyViewMapper;
import at.fhtw.society.backend.lobby.repo.LobbyMemberRepository;
import at.fhtw.society.backend.lobby.repo.LobbyRepository;
import at.fhtw.society.backend.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class LobbyCommandService {

    private final LobbyRepository lobbyRepository;
    private final LobbyMemberRepository lobbyMemberRepository;
    private final ThemeRepository themeRepository;
    private final PasswordEncoder passwordEncoder;
    private final LobbyViewMapper lobbyViewMapper;

    public LobbyCommandService(LobbyRepository lobbyRepository, LobbyMemberRepository lobbyMemberRepository, ThemeRepository themeRepository, PasswordEncoder passwordEncoder, LobbyViewMapper lobbyViewMapper) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyMemberRepository = lobbyMemberRepository;
        this.themeRepository = themeRepository;
        this.passwordEncoder = passwordEncoder;
        this.lobbyViewMapper = lobbyViewMapper;
    }

    /**
     * Creates a new lobby with the given parameters.
     * The creating player is assigned as the gamemaster of the lobby.
     * @param createLobbyRequestDto - DTO containing lobby creation parameters
     * @param identity - Identity of the creating player
     * @return DTO representing the created lobby
     */
    @Transactional
    public LobbyViewDto createLobby(CreateLobbyRequestDto createLobbyRequestDto, JwtService.PlayerIdentity identity) {
        // enforce one lobby at a time
        if (lobbyMemberRepository.existsByPlayerId(identity.playerId())) {
            throw new PlayerAlreadyInLobbyException(identity.playerId());
        }

        // validate theme exists
        Theme theme = themeRepository.findById(createLobbyRequestDto.getThemeId())
                .orElseThrow(() -> new ThemeNotFoundException(createLobbyRequestDto.getThemeId()));

        // Store only hashed password if provided
        String passwordHash = null;
        if (createLobbyRequestDto.getPassword() != null && !createLobbyRequestDto.getPassword().isBlank()) {
            passwordHash = passwordEncoder.encode(createLobbyRequestDto.getPassword());
        }

        // Create lobby entity
        Lobby lobby = Lobby.builder()
                .name(createLobbyRequestDto.getName())
                .theme(theme)
                .maxPlayers(createLobbyRequestDto.getMaxPlayers())
                .maxRounds(createLobbyRequestDto.getMaxRounds())
                .passwordHash(passwordHash)
                .status(LobbyStatus.OPEN)
                .build();

        // Create lobbymember entity for creator as gamemaster
        LobbyMember creator = LobbyMember.builder()
                .lobby(lobby)
                .playerId(identity.playerId())
                .name(identity.name())
                .avatarId(identity.avatarId())
                .role(LobbyRole.GAMEMASTER)
                .ready(false)
                .build();

        // Add the creator to the lobby's members
        lobby.getMembers().add(creator);

        // Save lobby in database
        Lobby savedLobby = lobbyRepository.save(lobby);

        return lobbyViewMapper.toDto(savedLobby);
    }

    /**
     * Player leaves the lobby. If the player is the gamemaster, a new gamemaster is assigned.
     * If the lobby becomes empty, it is deleted.
     * @param lobbyId - ID of the lobby to leave
     * @param playerId - ID of the player leaving the lobby
     */
    @Transactional
    public void leaveLobby(UUID lobbyId, UUID playerId) {
        if (!lobbyRepository.existsById(lobbyId)) throw new LobbyNotFoundException(lobbyId);

        LobbyMember leavingMember = lobbyMemberRepository.findByLobby_IdAndPlayerId(lobbyId, playerId)
                .orElseThrow(() -> new LobbyMemberNotFoundException(lobbyId, playerId));

        // true if the leaving member is the game master
        boolean wasGamemaster = leavingMember.getRole() == LobbyRole.GAMEMASTER;

        lobbyMemberRepository.delete(leavingMember);

        boolean isLobbyEmpty = lobbyMemberRepository.countByLobby_Id(lobbyId) == 0L;

        if (isLobbyEmpty) {
            lobbyRepository.deleteById(lobbyId);
        } else if (wasGamemaster) {
            Optional<LobbyMember> newGamemaster = lobbyMemberRepository.findFirstByLobby_IdOrderByJoinedAtAsc(lobbyId);
            newGamemaster.ifPresent(member -> {
                member.setRole(LobbyRole.GAMEMASTER);
                lobbyMemberRepository.save(member);
            });
        }
    }
}
