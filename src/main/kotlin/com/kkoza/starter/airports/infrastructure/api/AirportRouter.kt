package com.kkoza.starter.airports.infrastructure.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class AirportRouter(private val flightHandler: FlightHandler) {

    @Bean
    fun getConnections() =
            RouterFunctions.route(
                    RequestPredicates.GET("/connections"),
                    HandlerFunction<ServerResponse> { flightHandler.getConnections(it) }
            )

    @Bean
    fun getAirports() =
            RouterFunctions.route(
                    RequestPredicates.GET("/airports"),
                    HandlerFunction<ServerResponse> { flightHandler.getAirports(it) }
            )

    @Bean
    fun getRoutes() =
            RouterFunctions.route(
                    RequestPredicates.GET("/routes"),
                    HandlerFunction<ServerResponse> { flightHandler.getRoutes(it) }
            )

}
