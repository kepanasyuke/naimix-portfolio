package ru.naimix.auth.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import javax.crypto.SecretKey;

/**
 * Builds a Nimbus key source that exposes a single HS256 signing key.
 */
final class HmacKeySource {

    private final SecretKey key;

    /**
     * Creates the source.
     *
     * @param key symmetric key
     */
    HmacKeySource(final SecretKey key) {
        this.key = key;
    }

    /**
     * Returns a key source with an algorithm-bound JWK.
     *
     * @return Nimbus key source
     */
    JWKSource<SecurityContext> source() {
        return new ImmutableJWKSet<>(new JWKSet(jwk()));
    }

    private OctetSequenceKey jwk() {
        return new OctetSequenceKey.Builder(key).algorithm(JWSAlgorithm.HS256).build();
    }
}
