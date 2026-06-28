package com.minibank.api_gateway.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Value("${jwt.signerkey}")
    private String secret;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/user/register", "/auth/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oAuth2ResourceServer -> 
                oAuth2ResourceServer.jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            )
            
            .build();
    }
    @Bean
    ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec key =
                new SecretKeySpec(secret.getBytes(), "HmacSHA512");

        return NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}