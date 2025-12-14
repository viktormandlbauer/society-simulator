package at.fhtw.society.backend.security.jwt;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for JWT (JSON Web Token) security.
 * These properties are loaded from the application's configuration files
 * with the prefix "security.jwt".
 */

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    /**
     * The secret key used for signing and verifying JWTs.
     */
    @NotBlank(message = "security.jwt.secret must not be blank")
    private String secret;

    /**
     * The time-to-live (TTL) of the JWT in minutes.
     */
    @Min(value = 1, message = "security.jwt.ttl-minutes must be at least 1")
    private long ttlMinutes = 120;

    /**
     * The issuer of the JWT to help identify the token's origin.
     */
    @NotBlank(message = "security.jwt.issuer must not be blank")
    private String issuer = "society-simulator-backend";
}
