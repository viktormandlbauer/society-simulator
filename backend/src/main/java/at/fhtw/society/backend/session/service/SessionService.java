package at.fhtw.society.backend.session.service;

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

        // TODO: Implement JWT token generation logic here
        // Token should include: sub=playerId, claims(name, avatarId, role), issuedAt, expiration

        // For now, we explicitly fail fast to indicate this is not yet implemented
        throw new UnsupportedOperationException("JWT token generation not yet implemented.");

        // When JWT is implemented, return the response DTO like this:
//        return GuestSessionResponseDto.builder()
//                .playerId(playerId.toString())
//                .name(normalizedName)
//                .avatarId(request.getAvatarId())
//                .token(token)
//                .build();
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
