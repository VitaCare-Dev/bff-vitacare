package com.grupo10.bff_vitacare.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración de seguridad del BFF.
 *
 * <p>Valida el ID Token de Firebase Authentication (firma, issuer, expiración
 * y audiencia) en cada request a {@code /api/**}. Es el único punto del
 * backend donde se valida el token; los microservicios de dominio confían en
 * el tráfico proveniente del BFF.</p>
 */
@Configuration
public class SecurityConfig {

    private final String firebaseProjectId;
    private final String firebaseJwkSetUri;
    private final List<String> allowedOrigins;

    /**
     * @param firebaseProjectId id del proyecto de Firebase contra el que se valida
     *                          el issuer y la audiencia de los ID Tokens recibidos
     * @param firebaseJwkSetUri JWKS contra el que se verifica la firma de los ID Tokens;
     *                          por defecto, el que Google documenta para Firebase Authentication
     * @param allowedOrigins    orígenes web autorizados para CORS, separados por coma; vacío por
     *                          defecto (la app móvil no depende de CORS, solo un cliente web lo haría)
     */
    public SecurityConfig(
            @Value("${firebase.project-id}") String firebaseProjectId,
            @Value("${firebase.jwk-set-uri:https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com}")
            String firebaseJwkSetUri,
            @Value("${cors.allowed-origins:}") List<String> allowedOrigins) {
        this.firebaseProjectId = firebaseProjectId;
        this.firebaseJwkSetUri = firebaseJwkSetUri;
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * Decodificador de JWT que valida firma, issuer, expiración y audiencia
     * contra Firebase. Se usa el JWKS fijo de Firebase (en vez de descubrirlo
     * por {@code issuer-uri}) para que las claves se resuelvan de forma
     * perezosa en la primera verificación, sin requerir red al arrancar
     * la aplicación.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String issuer = "https://securetoken.google.com/" + firebaseProjectId;
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(firebaseJwkSetUri)
                .build();

        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> jwt.getAudience().contains(firebaseProjectId)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token", "El token no corresponde a este proyecto de Firebase", null));

        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidators, audienceValidator));

        return jwtDecoder;
    }

    /**
     * Define la cadena de filtros de seguridad: habilita CORS, deshabilita CSRF
     * (API sin estado consumida por un cliente móvil, no por un navegador con
     * sesión), exige autenticación JWT en {@code /api/**} y deja el resto de
     * rutas abiertas.
     *
     * @param http builder de configuración HTTP de Spring Security
     * @return la cadena de filtros de seguridad ya construida
     * @throws Exception si Spring Security falla al construir la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

        return http.build();
    }

    /**
     * Construye la política CORS aplicada a {@code /api/**}.
     *
     * <p>La app móvil no depende de CORS (solo aplica a navegadores), así que
     * por defecto ({@code cors.allowed-origins} vacío) no se autoriza ningún
     * origen web: usar {@code "*"} amplía innecesariamente la superficie de
     * ataque de una API pensada para un único cliente móvil. Si en el futuro
     * se necesita un cliente web (ej. panel de administración), sus orígenes
     * se configuran explícitamente vía esa propiedad.
     *
     * @return la fuente de configuración CORS para las rutas de la API
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

}
