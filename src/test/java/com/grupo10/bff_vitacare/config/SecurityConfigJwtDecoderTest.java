package com.grupo10.bff_vitacare.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;

/**
 * Prueba el decodificador de JWT de {@link SecurityConfig} de punta a punta,
 * incluyendo el validador de audiencia (custom), firmando tokens reales con
 * una llave RSA generada en memoria y sirviendo su JWKS desde un servidor
 * HTTP local — sin depender de red externa ni de Firebase real.
 */
class SecurityConfigJwtDecoderTest {

    private static final String PROJECT_ID = "vitacare-test";
    private static final String ISSUER = "https://securetoken.google.com/" + PROJECT_ID;

    private HttpServer jwkServer;
    private RSAKey rsaKey;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() throws Exception {
        rsaKey = new RSAKeyGenerator(2048).keyID("test-key").generate();

        jwkServer = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        jwkServer.createContext("/jwks", exchange -> {
            byte[] body = new JWKSet(rsaKey.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        jwkServer.start();

        String jwkSetUri = "http://localhost:" + jwkServer.getAddress().getPort() + "/jwks";
        SecurityConfig securityConfig = new SecurityConfig(PROJECT_ID, jwkSetUri);
        jwtDecoder = securityConfig.jwtDecoder();
    }

    @AfterEach
    void tearDown() {
        jwkServer.stop(0);
    }

    private String signedToken(String audience) throws Exception {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("uid-1")
                .issuer(ISSUER)
                .audience(audience)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(300)))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).keyID("test-key").build(),
                claims);
        signedJWT.sign(new RSASSASigner(rsaKey));
        return signedJWT.serialize();
    }

    @Test
    void decodesATokenWithTheMatchingAudience() throws Exception {
        Jwt jwt = jwtDecoder.decode(signedToken(PROJECT_ID));

        assertThat(jwt.getSubject()).isEqualTo("uid-1");
    }

    @Test
    void rejectsATokenWithAMismatchedAudience() throws Exception {
        String token = signedToken("otro-proyecto");

        assertThatThrownBy(() -> jwtDecoder.decode(token))
                .isInstanceOf(JwtValidationException.class)
                .hasMessageContaining("no corresponde a este proyecto");
    }
}
