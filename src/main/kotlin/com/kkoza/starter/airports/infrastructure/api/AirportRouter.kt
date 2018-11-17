package com.kkoza.starter.airports.infrastructure.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class AirportRouter(private val airportHandler: AirportHandler) {

    @Bean
    fun getConnections() =
            RouterFunctions.route(
                    RequestPredicates.GET("/connections"),
                    HandlerFunction<ServerResponse> { airportHandler.getConnections(it) }
            )

    @Bean
    fun getAirports() =
            RouterFunctions.route(
                    RequestPredicates.GET("/airports"),
                    HandlerFunction<ServerResponse> { airportHandler.getAirports(it) }
            )

}
