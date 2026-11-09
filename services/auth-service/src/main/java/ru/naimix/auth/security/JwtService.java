package ru.naimix.auth.security;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Issues HMAC signed access tokens through the Spring Security encoder.
 */
@Service
public class JwtService {

    private static final String ISSUER = "naimix-auth";
    private static final String ROLE_CLAIM = "role";

    private final JwtEncoder encoder;
    private final Duration ttl;

    /**
     * Creates the service from configuration.
     *
     * @param encoder JWT encoder
     * @param ttl access token lifetime
     */
    public JwtService(final JwtEncoder encoder, @Value("${auth.jwt.ttl}") final Duration ttl) {
        this.encoder = encoder;
        this.ttl = ttl;
    }

    /**
     * Issues a signed access token.
     *
     * @param subject account email
     * @param role account role
     * @return compact JWT
     */
    public String issue(final String subject, final String role) {
        final JwtClaimsSet claims = claims(subject, role, Instant.now());
        return encoder.encode(JwtEncoderParameters.from(header(), claims)).getTokenValue();
    }

    private JwsHeader header() {
        return JwsHeader.with(MacAlgorithm.HS256).build();
    }

    /**
     * Returns the access token lifetime in seconds.
     *
     * @return lifetime in seconds
     */
    public long ttlSeconds() {
        return ttl.toSeconds();
    }

    /**
     * Returns the configured lifetime.
     *
     * @return lifetime
     */
    public Duration ttl() {
        return ttl;
    }

    private JwtClaimsSet claims(final String subject, final String role, final Instant issuedAt) {
        final var builder = JwtClaimsSet.builder().issuer(ISSUER).subject(subject).id(UUID.randomUUID().toString());
        return builder.claim(ROLE_CLAIM, role).issuedAt(issuedAt).expiresAt(issuedAt.plus(ttl)).build();
    }
}
