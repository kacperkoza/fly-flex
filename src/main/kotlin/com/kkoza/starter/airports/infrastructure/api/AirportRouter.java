package com.kkoza.starter.airports.infrastructure.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AirportRouter {

    private final ConnectionsHandler connectionsHandler;

    public AirportRouter(ConnectionsHandler connectionsHandler) {
        this.connectionsHandler = connectionsHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> getConnections() {
        return RouterFunctions.route(
                RequestPredicates.GET("/connections"),
                connectionsHandler::getConnections
        );
    }


}
