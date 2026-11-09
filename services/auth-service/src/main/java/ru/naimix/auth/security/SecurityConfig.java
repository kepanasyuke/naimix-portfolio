package ru.naimix.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Stateless OAuth 2.0 resource server configuration.
 */
@Configuration
public class SecurityConfig {

    private static final String ROLE_CLAIM = "role";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String[] OPEN_PATHS = {"/api/auth/register", "/api/auth/login", "/actuator/health/**"};

    /**
     * Builds the security filter chain.
     *
     * @param http HTTP security builder
     * @return configured chain
     * @throws Exception when the chain cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return withAuthentication(withRules(stateless(withoutCsrf(http)))).build();
    }

    /**
     * Password hashing strategy.
     *
     * @return bcrypt encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private HttpSecurity withoutCsrf(final HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable);
    }

    private HttpSecurity stateless(final HttpSecurity http) throws Exception {
        return http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    private HttpSecurity withRules(final HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(registry -> registry.requestMatchers(OPEN_PATHS).permitAll()
                .anyRequest().authenticated());
    }

    private HttpSecurity withAuthentication(final HttpSecurity http) throws Exception {
        return http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter())))
                .exceptionHandling(handling -> handling.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
    }

    private JwtAuthenticationConverter converter() {
        return converterOf(authorities());
    }

    private JwtGrantedAuthoritiesConverter authorities() {
        return configured(new JwtGrantedAuthoritiesConverter());
    }

    private JwtGrantedAuthoritiesConverter configured(final JwtGrantedAuthoritiesConverter authorities) {
        authorities.setAuthoritiesClaimName(ROLE_CLAIM);
        return prefixed(authorities);
    }

    private JwtGrantedAuthoritiesConverter prefixed(final JwtGrantedAuthoritiesConverter authorities) {
        authorities.setAuthorityPrefix(ROLE_PREFIX);
        return authorities;
    }

    private JwtAuthenticationConverter converterOf(final JwtGrantedAuthoritiesConverter authorities) {
        return withAuthorities(new JwtAuthenticationConverter(), authorities);
    }

    private JwtAuthenticationConverter withAuthorities(final JwtAuthenticationConverter converter,
                                                       final JwtGrantedAuthoritiesConverter authorities) {
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
