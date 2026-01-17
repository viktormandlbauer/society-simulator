package at.fhtw.society.backend.lobby.service;

import at.fhtw.society.backend.game.entity.Theme;
import at.fhtw.society.backend.game.repo.ThemeRepository;
import at.fhtw.society.backend.lobby.dto.CreateLobbyRequestDto;
import at.fhtw.society.backend.lobby.dto.JoinLobbyRequestDto;
import at.fhtw.society.backend.lobby.dto.LobbyViewDto;
import at.fhtw.society.backend.lobby.entity.Lobby;
import at.fhtw.society.backend.lobby.entity.LobbyMember;
import at.fhtw.society.backend.lobby.entity.LobbyRole;
import at.fhtw.society.backend.lobby.entity.LobbyStatus;
import at.fhtw.society.backend.lobby.exception.*;
import at.fhtw.society.backend.lobby.mapper.LobbyViewMapper;
import at.fhtw.society.backend.lobby.repo.LobbyMemberRepository;
import at.fhtw.society.backend.lobby.repo.LobbyRepository;
import at.fhtw.society.backend.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
     * Player joins an existing lobby. Validations are performed to ensure the player can join.
     * If successful, the updated lobby view is returned.
     * If the player is already in the lobby, the current lobby view is returned.
     * @param lobbyId - ID of the lobby to join
     * @param joinLobbyRequestDto - DTO containing join parameters (password)
     * @param identity - Identity of the joining player
     * @return DTO representing the updated lobby view
     */
    @Transactional
    public LobbyViewDto joinLobby(UUID lobbyId, JoinLobbyRequestDto joinLobbyRequestDto, JwtService.PlayerIdentity identity) {
        // check if already in this lobby, if so, return current lobby view
        if (lobbyMemberRepository.existsByLobby_IdAndPlayerId(lobbyId, identity.playerId())) {
            Lobby lobby = lobbyRepository.findByIdForJoin(lobbyId)
                    .orElseThrow(() -> new LobbyNotFoundException(lobbyId));
            lobby.getMembers().sort(Comparator.comparing(LobbyMember::getJoinedAt));
            return lobbyViewMapper.toDto(lobby);
        }

        // enforce one lobby at a time
        if (lobbyMemberRepository.existsByPlayerId(identity.playerId())) {
            throw new PlayerAlreadyInLobbyException(identity.playerId());
        }

        // lock lobby row to prevent race conditions on max players
        Lobby lobby = lobbyRepository.findByIdForJoin(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId));

        if (lobby.getStatus() != LobbyStatus.OPEN) {
            throw new LobbyNotJoinableException(lobbyId, lobby.getStatus());
        }

        int currentMemberCount = lobby.getMembers().size();
        if (currentMemberCount >= lobby.getMaxPlayers()) {
            throw new LobbyFullException(lobbyId);
        }

        // verify password if lobby is protected
        if (lobby.hasPassword()) {
            String rawPassword = (joinLobbyRequestDto == null) ? null : joinLobbyRequestDto.getPassword();
            if (rawPassword == null || rawPassword.isBlank()) {
                throw new LobbyPasswordInvalidException(lobbyId);
            }
            if (!passwordEncoder.matches(rawPassword, lobby.getPasswordHash())) {
                throw new LobbyPasswordInvalidException(lobbyId);
            }
        }

        LobbyMember newMember = LobbyMember.builder()
                .lobby(lobby)
                .playerId(identity.playerId())
                .name(identity.name())
                .avatarId(identity.avatarId())
                .role(LobbyRole.PLAYER)
                .ready(false)
                .build();

        lobby.getMembers().add(newMember);

        Lobby updatedLobby = lobbyRepository.save(lobby);

        updatedLobby.getMembers().sort(Comparator.comparing(LobbyMember::getJoinedAt));

        return lobbyViewMapper.toDto(updatedLobby);
    }

    /**
     * Player leaves the lobby. If the player is the gamemaster, a new gamemaster is assigned.
     * If the lobby becomes empty, it is deleted (unless a game has been created for it).
     * @param lobbyId - ID of the lobby to leave
     * @param playerId - ID of the player leaving the lobby
     */
    @Transactional
    public void leaveLobby(UUID lobbyId, UUID playerId) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId));

        LobbyMember leavingMember = lobbyMemberRepository.findByLobby_IdAndPlayerId(lobbyId, playerId)
                .orElseThrow(() -> new LobbyMemberNotFoundException(lobbyId, playerId));

        // true if the leaving member is the game master
        boolean wasGamemaster = leavingMember.getRole() == LobbyRole.GAMEMASTER;

        lobbyMemberRepository.delete(leavingMember);

        boolean isLobbyEmpty = lobbyMemberRepository.countByLobby_Id(lobbyId) == 0L;

        // Only delete the lobby if it's empty AND no game has been created for it
        if (isLobbyEmpty) {
            // Check if a game exists for this lobby
            if (lobby.getGame() == null) {
                lobbyRepository.deleteById(lobbyId);
            }
            // If a game exists, just leave the lobby empty (game still references it)
        } else if (wasGamemaster) {
            Optional<LobbyMember> newGamemaster = lobbyMemberRepository.findFirstByLobby_IdOrderByJoinedAtAsc(lobbyId);
            newGamemaster.ifPresent(member -> {
                member.setRole(LobbyRole.GAMEMASTER);
                lobbyMemberRepository.save(member);
            });
        }
    }

    /**
     * Verifies that the given player is the gamemaster of the specified lobby.
     * @param lobbyId - ID of the lobby
     * @param playerId - ID of the player to verify
     * @throws LobbyNotFoundException if the lobby does not exist
     * @throws LobbyMemberNotFoundException if the player is not a member of the lobby
     * @throws NotGamemasterException if the player is not the gamemaster
     */
    public void verifyGamemaster(UUID lobbyId, UUID playerId) {
        if (!lobbyRepository.existsById(lobbyId)) {
            throw new LobbyNotFoundException(lobbyId);
        }

        LobbyMember member = lobbyMemberRepository.findByLobby_IdAndPlayerId(lobbyId, playerId)
                .orElseThrow(() -> new LobbyMemberNotFoundException(lobbyId, playerId));

        if (member.getRole() != LobbyRole.GAMEMASTER) {
            throw new NotGamemasterException(lobbyId, playerId);
        }
    }
}
