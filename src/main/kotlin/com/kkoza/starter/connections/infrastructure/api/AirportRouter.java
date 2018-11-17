package com.kkoza.starter.connections.infrastructure.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AirportRouter {

    @Bean
    public RouterFunction<ServerResponse> getConnections() {
        return RouterFunctions.route(
                RequestPredicates.GET("/connections"),
                request ->  ServerResponse.ok().body(BodyInserters.empty())
        );
    }

}
