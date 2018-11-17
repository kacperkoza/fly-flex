package com.kkoza.starter.airports.infrastructure.api

import com.kkoza.starter.airports.Airport

import com.kkoza.starter.airports.FlightFacade
import com.kkoza.starter.airports.infrastructure.api.dto.AirportDto
import com.kkoza.starter.airports.infrastructure.api.dto.AirportResponse
import com.kkoza.starter.airports.infrastructure.api.dto.ConnectionsResponse
import com.kkoza.starter.airports.infrastructure.api.dto.RouteResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class FlightHandler(private val flightFacade: FlightFacade) {

    companion object {
        private const val FIRST_IATA_PARAM = "firstIata"
        private const val SECOND_IATA_PARAM = "secondIata"
        private const val DESTINATION_IATA_PARAM = "destinationIata"
    }

    fun getConnections(request: ServerRequest): Mono<ServerResponse> {
        val firstIata = getQueryParamFromRequest(request, FIRST_IATA_PARAM) // i put my trust in the client
        val secondIata = getQueryParamFromRequest(request, SECOND_IATA_PARAM)

        val response = flightFacade.getConnections(firstIata, secondIata)
                .map { this.mapToAirportDto(it) }
                .collectList()
                .map { c -> mapToAirportResponse(c, firstIata, secondIata) }

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(response, ConnectionsResponse::class.java))
    }

    fun getAirports(request: ServerRequest): Mono<ServerResponse> {
        val airportsResponse = flightFacade.getAllAirports()
                .map { this.mapToAirportDto(it) }
                .collectList()
                .map { this.mapToAirportResponse(it) }
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(airportsResponse, AirportResponse::class.java))
    }

    fun getRoutes(request: ServerRequest): Mono<ServerResponse> {
        val firstIata = getQueryParamFromRequest(request, FIRST_IATA_PARAM)
        val secondIata = getQueryParamFromRequest(request, SECOND_IATA_PARAM)
        val destinationIata = getQueryParamFromRequest(request, DESTINATION_IATA_PARAM)

        val routes = flightFacade.findRoutes(firstIata, secondIata, destinationIata)
                .map { this.mapToRoutes()!! }

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(
                        routes,
                        RouteResponse::class.java))
    }

    private fun mapToRoutes(): RouteResponse? {
        return null
    }

    private fun getQueryParamFromRequest(request: ServerRequest, departureAirportParam: String): String {
        return request.queryParam(departureAirportParam).get()
    }

    private fun mapToAirportResponse(connections: List<AirportDto>, departure: String, destination: String): ConnectionsResponse {
        return ConnectionsResponse(departure, destination, connections)
    }

    private fun mapToAirportResponse(airports: List<AirportDto>): AirportResponse {
        return AirportResponse(airports)
    }

    private fun mapToAirportDto(airport: Airport): AirportDto {
        return AirportDto(airport.cityName, airport.iataCode)
    }
}
