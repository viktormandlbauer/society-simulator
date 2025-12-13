package at.fhtw.society.backend.security.jwt;

import at.fhtw.society.backend.session.dto.AvatarId;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JwtService {

    public static final String CLAIM_NAME = "name";
    public static final String CLAIM_AVATAR_ID = "avatarId";
    public static final String CLAIM_ROLE = "role";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;

    public JwtService(JwtEncoder jwtEncoder,
                      JwtDecoder jwtDecoder,
                      JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Issues a JWT token for the given player information.
     * Claims included in the token are:
     * - sub: playerId
     * - iss: identifies our backend as the token origin (validated on decode)
     * - iat: issued at timestamp
     * - exp: expiration timestamp (calculated using TTL from properties)
     * - name: player's display name
     * - avatarId: player's chosen avatar ID
     * - role: player's role
     *
     * @param playerId the unique identifier of the player
     * @param name     the display name of the player
     * @param avatarId the avatar ID chosen by the player
     * @param role     the role assigned to the player (e.g., "GUEST")
     * @return an IssuedToken containing the JWT token and its expiration time
     */
    public IssuedToken issuePlayerToken(UUID playerId,
                                        String name,
                                        AvatarId avatarId,
                                        String role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtProperties.getTtlMinutes() * 60);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(playerId.toString())
                .claim(CLAIM_NAME, name)
                .claim(CLAIM_AVATAR_ID, avatarId.getId())
                .claim(CLAIM_ROLE, role)
                .build();

        // Explicitly set HS256 algorithm in the headers
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtEncoderParameters params = JwtEncoderParameters.from(header, claims);

        String tokenValue = jwtEncoder.encode(params).getTokenValue();
        return new IssuedToken(tokenValue, expiresAt);
    }

    /**
     * Decodes and validates the given JWT token.
     * Validation covered by JwtDecoder + validator configured in JwtCryptoConfig:
     * - signatur (using HS256 and our secret key)
     * - issuer
     * - expiration time
     *
     * @param token the JWT token string
     * @return the decoded Jwt object
     * @throws JwtException if the token is invalid or cannot be decoded
     */
    public Jwt decodeAndValidate(String token) throws JwtException {
        return jwtDecoder.decode(token);
    }

    /**
     * Converts the given Jwt object into a PlayerIdentity.
     *
     * @param jwt the Jwt object
     * @return the PlayerIdentity extracted from the Jwt
     * @throws JwtException if required claims are missing or invalid
     */
    public PlayerIdentity toPlayerIdentity(Jwt jwt) {
        UUID playerId = UUID.fromString(jwt.getSubject());
        String name = getRequiredStringClaim(jwt, CLAIM_NAME);
        String avatarIdRaw = getRequiredStringClaim(jwt, CLAIM_AVATAR_ID);
        String role = getRequiredStringClaim(jwt, CLAIM_ROLE);

        AvatarId avatarId = AvatarId.fromValue(avatarIdRaw);
        return new PlayerIdentity(playerId, name, avatarId, role);
    }

    /**
     * Reads a required string claim from the given Jwt.
     *
     * @param jwt       the Jwt object
     * @param claimName the name of the claim to retrieve
     * @return the string value of the claim
     * @throws JwtException if the claim is missing or not a valid non-blank string
     */
    private String getRequiredStringClaim(Jwt jwt, String claimName) {
        Object claimValue = jwt.getClaims().get(claimName);
        if (!(claimValue instanceof String s) || s.isBlank()) {
            throw new JwtException("Missing or invalid claim: " + claimName);
        }
        return s;
    }

    /**
     * Represents an issued JWT token along with its expiration time.
     */
    public record IssuedToken(String token, Instant expiresAt) {
    }

    /**
     * Represents the identity of a player extracted from a JWT token.
     */
    public record PlayerIdentity(UUID playerId, String name, AvatarId avatarId, String role) {
    }

    // TODO: When we add LOGIN functionality in addition to GUEST, we will need
    // we can:
    // add userId/email to the token claims
    // support multiple token types (guest, user)
    // add refresh tokens
}
