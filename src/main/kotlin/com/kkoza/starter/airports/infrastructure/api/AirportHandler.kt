package com.kkoza.starter.airports.infrastructure.api

import com.kkoza.starter.airports.Airport

import com.kkoza.starter.airports.AirportFacade
import com.kkoza.starter.airports.infrastructure.api.dto.AirportDto
import com.kkoza.starter.airports.infrastructure.api.dto.AirportResponse
import com.kkoza.starter.airports.infrastructure.api.dto.ConnectionsResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class AirportHandler(private val airportFacade: AirportFacade) {

    companion object {
        private const val DEPARTURE_AIRPORT_PARAM = "firstIata"
        private const val DESTINATION_AIRPORT_PARAM = "secondIata"
    }

    fun getConnections(request: ServerRequest): Mono<ServerResponse> {
        val departure = getQueryParamFromRequest(request, DEPARTURE_AIRPORT_PARAM) // i put my trust in the client
        val destination = getQueryParamFromRequest(request, DESTINATION_AIRPORT_PARAM)

        val connections = airportFacade.getConnections(departure, destination)
                .map { this.mapToAirportDto(it) }
                .collectList()

        val response = connections
                .map { c -> mapToAirportResponse(c, departure, destination) }

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(response, ConnectionsResponse::class.java))
    }

    fun getAirports(request: ServerRequest): Mono<ServerResponse> {
        val airportsDto = airportFacade.getAllAirports()
                .map { this.mapToAirportDto(it) }
                .collectList()

        val airports = airportsDto.map { this.mapToAirportResponse(it) }
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(airports, AirportResponse::class.java))
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
