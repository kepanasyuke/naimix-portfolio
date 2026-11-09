package ru.naimix.auth.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import ru.naimix.auth.service.TokenStore;

/**
 * Symmetric JWT encoder and decoder wiring.
 */
@Configuration
public class JwtConfig {

    private static final String ALGORITHM = "HmacSHA256";

    private final String secret;
    private final TokenStore tokenStore;

    /**
     * Creates the configuration.
     *
     * @param secret HMAC secret
     * @param tokenStore revocation list
     */
    public JwtConfig(@Value("${auth.jwt.secret}") final String secret, final TokenStore tokenStore) {
        this.secret = secret;
        this.tokenStore = tokenStore;
    }

    /**
     * Encoder used to issue access tokens.
     *
     * @return JWT encoder
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new HmacKeySource(key()).source());
    }

    /**
     * Decoder used to verify access tokens, including the revocation list.
     *
     * @return JWT decoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return withRevocation(NimbusJwtDecoder.withSecretKey(key()).build());
    }

    private SecretKey key() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    private JwtDecoder withRevocation(final NimbusJwtDecoder decoder) {
        decoder.setJwtValidator(validators());
        return decoder;
    }

    private OAuth2TokenValidator<Jwt> validators() {
        return new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(), new RevocationValidator(tokenStore));
    }
}
