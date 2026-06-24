package com.minibank.api_gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Configuration
public class GatewayConfiguration {

    private static final String HTTP_HEADER_AUTH_USER_ID = "X-Auth-Id";
    private static final String UNAUTHORIZED_USER_NAME = "SYSTEM USER";

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> 
            exchange.getPrincipal()
                .map(principal -> principal.getName())
                .defaultIfEmpty(UNAUTHORIZED_USER_NAME)
                .flatMap(username -> {
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header(HTTP_HEADER_AUTH_USER_ID, username)
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }
}