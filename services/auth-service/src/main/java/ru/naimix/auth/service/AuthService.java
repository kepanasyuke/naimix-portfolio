package ru.naimix.auth.service;

import java.time.Duration;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.naimix.auth.api.dto.LoginRequest;
import ru.naimix.auth.api.dto.RegisterRequest;
import ru.naimix.auth.api.dto.TokenResponse;
import ru.naimix.auth.api.dto.UserView;
import ru.naimix.auth.domain.UserAccount;
import ru.naimix.auth.domain.UserRepository;
import ru.naimix.auth.error.EmailAlreadyUsedException;
import ru.naimix.auth.error.RejectedCredentialsException;
import ru.naimix.auth.security.JwtService;

/**
 * Registration, authentication and token revocation.
 */
@Service
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final Duration MIN_TTL = Duration.ofSeconds(1);

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final TokenStore tokenStore;

    /**
     * Creates the service.
     *
     * @param users account repository
     * @param encoder password encoder
     * @param jwtService token service
     * @param tokenStore revocation list
     */
    public AuthService(final UserRepository users, final PasswordEncoder encoder,
                       final JwtService jwtService, final TokenStore tokenStore) {
        this.users = users;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
    }

    /**
     * Registers a new account and issues a token.
     *
     * @param request registration payload
     * @return issued token
     */
    @Transactional
    public TokenResponse register(final RegisterRequest request) {
        return issue(createAccount(request));
    }

    /**
     * Authenticates an account and issues a token.
     *
     * @param request login payload
     * @return issued token
     */
    @Transactional(readOnly = true)
    public TokenResponse login(final LoginRequest request) {
        return issue(authenticate(request));
    }

    /**
     * Revokes the presented access token.
     *
     * @param token verified access token
     */
    public void logout(final Jwt token) {
        tokenStore.revoke(token.getId(), remaining(token));
    }

    /**
     * Returns the public projection of an account.
     *
     * @param email account email
     * @return account view
     */
    @Transactional(readOnly = true)
    public UserView currentUser(final String email) {
        return toView(requireUser(email));
    }

    private UserAccount createAccount(final RegisterRequest request) {
        guardEmailIsFree(request.email());
        return users.save(new UserAccount(request.email(), encoder.encode(request.password()), request.role()));
    }

    private void guardEmailIsFree(final String email) {
        if (users.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }
    }

    private UserAccount authenticate(final LoginRequest request) {
        return verified(requireUser(request.email()), request.password());
    }

    private UserAccount requireUser(final String email) {
        return users.findByEmail(email).orElseThrow(() -> new RejectedCredentialsException("account not found"));
    }

    private UserAccount verified(final UserAccount account, final String rawPassword) {
        guardPasswordMatches(account, rawPassword);
        return account;
    }

    private void guardPasswordMatches(final UserAccount account, final String rawPassword) {
        if (!encoder.matches(rawPassword, account.getPasswordHash())) {
            throw new RejectedCredentialsException("password does not match");
        }
    }

    private TokenResponse issue(final UserAccount account) {
        final String token = jwtService.issue(account.getEmail(), account.getRole());
        return new TokenResponse(token, TOKEN_TYPE, jwtService.ttlSeconds(), account.getEmail(), account.getRole());
    }

    private UserView toView(final UserAccount account) {
        return new UserView(account.getId(), account.getEmail(), account.getRole());
    }

    private Duration remaining(final Jwt token) {
        return clamp(Duration.between(Instant.now(), token.getExpiresAt()));
    }

    private Duration clamp(final Duration duration) {
        return duration.isNegative() ? MIN_TTL : duration;
    }
}
