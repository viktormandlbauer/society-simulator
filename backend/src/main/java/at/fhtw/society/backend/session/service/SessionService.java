package at.fhtw.society.backend.session.service;

import at.fhtw.society.backend.security.jwt.JwtService;
import at.fhtw.society.backend.session.exception.InvalidSessionRequestException;
import at.fhtw.society.backend.session.dto.GuestSessionRequestDto;
import at.fhtw.society.backend.session.dto.GuestSessionResponseDto;
import org.springframework.stereotype.Service;

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

    public SessionService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Creates a new guest session based on the provided request data.
     *
     * @param request the request DTO containing the player's name and avatar ID
     * @return a response DTO containing the player ID, normalized name, avatar ID, and JWT token
     * @throws InvalidSessionRequestException if the provided name is invalid after normalization
     */
    public GuestSessionResponseDto createGuestSession(GuestSessionRequestDto request) {
        String normalizedName = normalizeDisplayName(request.getName());

        if (normalizedName.isBlank()) {
            throw new InvalidSessionRequestException("Name must not be blank after trimming.");
        }

        UUID playerId = UUID.randomUUID();

        JwtService.IssuedToken issuedToken = jwtService.issuePlayerToken(
                playerId,
                normalizedName,
                request.getAvatarId(),
                ROLE_GUEST
        );

        return GuestSessionResponseDto.builder()
                .playerId(playerId.toString())
                .name(normalizedName)
                .avatarId(request.getAvatarId())
                .token(issuedToken.token())
                .expiresAt(issuedToken.expiresAt())
                .role(ROLE_GUEST)
                .build();

        // TODO:
        // Persist player: store playerId, normalizedName, avatarId, createdAt in the database
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
