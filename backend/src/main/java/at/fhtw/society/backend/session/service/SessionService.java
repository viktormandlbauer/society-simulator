package at.fhtw.society.backend.session.service;

import at.fhtw.society.backend.game.entity.Player;
import at.fhtw.society.backend.game.repo.PlayerRepository;
import at.fhtw.society.backend.security.jwt.JwtService;
import at.fhtw.society.backend.session.exception.InvalidSessionRequestException;
import at.fhtw.society.backend.session.dto.GuestSessionRequestDto;
import at.fhtw.society.backend.session.dto.GuestSessionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for creating and managing player sessions.
 * This service handles the business logic related to session creation,
 * including validation of input data and generation of session tokens.
 */
@Service
public class SessionService {

    private static final String ROLE_GUEST = "GUEST"; // TODO: unify with ROLE_* constants

    private final JwtService jwtService;
    private final PlayerRepository playerRepository;

    public SessionService(JwtService jwtService, PlayerRepository playerRepository) {
        this.jwtService = jwtService;
        this.playerRepository = playerRepository;
    }

    /**
     * Creates a new guest session based on the provided request data.
     * Persists the guest player in the database without requiring a password.
     *
     * @param request the request DTO containing the player's name and avatar ID
     * @return a response DTO containing the player ID, normalized name, avatar ID, and JWT token
     * @throws InvalidSessionRequestException if the provided name is invalid after normalization
     */
    @Transactional
    public GuestSessionResponseDto createGuestSession(GuestSessionRequestDto request) {
        String normalizedName = normalizeDisplayName(request.getName());

        if (normalizedName.isBlank()) {
            throw new InvalidSessionRequestException("Name must not be blank after trimming.");
        }

        // Create and persist the guest player
        Player guestPlayer = Player.builder()
                .name(normalizedName)
                .avatarId(request.getAvatarId())
                .isGuest(true)
                .build();

        Player savedPlayer = playerRepository.save(guestPlayer);

        // Generate JWT token for the guest player
        JwtService.IssuedToken issuedToken = jwtService.issuePlayerToken(
                savedPlayer.getId(),
                normalizedName,
                request.getAvatarId(),
                ROLE_GUEST
        );

        return GuestSessionResponseDto.builder()
                .playerId(savedPlayer.getId().toString())
                .name(normalizedName)
                .avatarId(request.getAvatarId())
                .token(issuedToken.token())
                .expiresAt(issuedToken.expiresAt())
                .role(ROLE_GUEST)
                .build();

        // TODO:
        // Login: createUserSession(LoginRequestDto) -> role "USER", subject = userId etc.
    }

    /**
     * Normalizes the display name by trimming leading/trailing whitespace
     * and replacing multiple consecutive whitespace characters with a single space.
     *
     * @param name the original display name
     * @return the normalized display name
     */
    private String normalizeDisplayName(String name) {
        if (name == null) {
            return "";
        }

        return name.trim().replaceAll("\\s+", " ");
    }
}
