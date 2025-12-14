package at.fhtw.society.backend.security.jwt;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * JWT crypto configuration for HS256 algorithm (HMAC with SHA-256).
 * - Builds a SecretKey for our configured "security.jwt.secret"
 * - Exposes a JwtEncoder bean (used to sign tokens)
 * - Exposes a JwtDecoder bean (used to verify and parse tokens)
 */

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtCryptoConfig {

    /**
     * Creates a SecretKey for HS256 signing and verification based on the configured JWT secret.
     * The secret can be provided either as a raw string or as a Base64-encoded string.
     * If the secret is Base64-encoded, it will be decoded before use.
     * The resulting key must be at least 256 bits (32 bytes) long.
     *
     * @param jwtProperties the JWT properties containing the secret
     * @return the SecretKey for HS256
     * @throws IllegalArgumentException if the secret key is shorter than 32 bytes
     */
    @Bean
    public SecretKey jwtSecretKey(JwtProperties jwtProperties) {
        String secret = jwtProperties.getSecret();

        // Try to decode the secret as Base64 first
        byte[] keyBytes = tryDecodeBase64(secret);

        // Fallback to raw bytes if not Base64-encoded. Treat it as a plain UTF-8 string.
        if (keyBytes == null) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) {
            throw new BeanCreationException(
                    "The JWT secret key must be at least 256 bits (32 bytes) long for HS256."
            );
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * Creates a JwtEncoder bean for signing JWTs using the HS256 algorithm.
     * JwtEncoder is used when we CREATE (issue) JWT tokens.
     * NimbusJwtEncoder signs tokens using the provided SecretKey.
     * That signature makes the token tamper-proof.
     * - anyone can read the payload (claims) of the token
     * - but only parties with the secret key can create or change tokens
     *
     * @param jwtSecretKey the SecretKey used for signing
     * @return the JwtEncoder
     */
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    /**
     * Creates a JwtDecoder bean for verifying and parsing JWTs using the HS256 algorithm.
     * JwtDecoder is used when we RECEIVE (validate) JWT tokens.
     * NimbusJwtDecoder verifies the token's signature using the provided SecretKey.
     * It also validates standard claims like issuer and expiration.
     *
     * @param jwtSecretKey the SecretKey used for verification
     * @param jwtProperties the JWT properties containing validation settings
     * @return the JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey, JwtProperties jwtProperties) {
        // Build the NimbusJwtDecoder with the secret key and HS256 algorithm
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // Create a validator that checks the issuer and standard claims
        OAuth2TokenValidator<Jwt> validator =
                // Use default validator with our expected issuer
                JwtValidators.createDefaultWithIssuer(jwtProperties.getIssuer());

        decoder.setJwtValidator(validator);
        return decoder;
    }

    // -------------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------------

    /**
     * Tries to decode the given input string as Base64.
     *
     * @param input the input string
     * @return the decoded byte array, or null if decoding fails
     */
    private byte[] tryDecodeBase64(String input) {
        try {
            return Base64.getDecoder().decode(input);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
