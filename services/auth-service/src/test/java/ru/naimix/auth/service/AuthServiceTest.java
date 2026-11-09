package ru.naimix.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.naimix.auth.api.dto.LoginRequest;
import ru.naimix.auth.api.dto.RegisterRequest;
import ru.naimix.auth.domain.UserAccount;
import ru.naimix.auth.domain.UserRepository;
import ru.naimix.auth.error.RejectedCredentialsException;
import ru.naimix.auth.security.JwtService;

class AuthServiceTest {

    private static final long TTL_SECONDS = 900L;
    private static final String EMAIL = "anna@example.com";
    private static final String TOKEN = "jwt-token";
    private static final String PASSWORD = "secret-password";

    private final UserRepository users = mock(UserRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final TokenStore tokenStore = mock(TokenStore.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AuthService service = new AuthService(users, encoder, jwtService, tokenStore);

    @Test
    void registersAccountAndIssuesToken() {
        givenFreeEmail();
        assertEquals(TOKEN, service.register(registerRequest()).accessToken());
    }

    @Test
    void rejectsLoginWithWrongPassword() {
        givenExistingAccount();
        assertThrows(RejectedCredentialsException.class, () -> service.login(loginRequest("wrong-password")));
    }

    private void givenFreeEmail() {
        stubFreeEmail();
        issuedToken();
    }

    private void stubFreeEmail() {
        when(users.existsByEmail(anyString())).thenReturn(false);
        when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void issuedToken() {
        when(jwtService.issue(anyString(), anyString())).thenReturn(TOKEN);
        when(jwtService.ttlSeconds()).thenReturn(TTL_SECONDS);
    }

    private void givenExistingAccount() {
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(account()));
    }

    private UserAccount account() {
        return new UserAccount(EMAIL, encoder.encode(PASSWORD), "FREELANCER");
    }

    private RegisterRequest registerRequest() {
        return new RegisterRequest(EMAIL, PASSWORD, "FREELANCER");
    }

    private LoginRequest loginRequest(final String password) {
        return new LoginRequest(EMAIL, password);
    }
}
