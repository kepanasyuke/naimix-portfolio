package ru.naimix.auth.api;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.naimix.auth.api.dto.LoginRequest;
import ru.naimix.auth.api.dto.RegisterRequest;
import ru.naimix.auth.api.dto.TokenResponse;
import ru.naimix.auth.api.dto.UserView;
import ru.naimix.auth.service.AuthService;

/**
 * Authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Creates the controller.
     *
     * @param authService authentication service
     */
    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new account.
     *
     * @param request registration payload
     * @return issued token
     */
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody final RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Authenticates an account.
     *
     * @param request login payload
     * @return issued token
     */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody final LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Revokes the current access token.
     *
     * @param token verified access token
     * @return empty 204 response
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal final Jwt token) {
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the current account.
     *
     * @param principal authenticated principal
     * @return account view
     */
    @GetMapping("/me")
    public UserView me(final Principal principal) {
        return authService.currentUser(principal.getName());
    }
}
