package com.kkoza.starter.airports.infrastructure.api

import com.kkoza.starter.airports.Airport

import com.kkoza.starter.airports.FlightFacade
import com.kkoza.starter.airports.Routes
import com.kkoza.starter.airports.infrastructure.api.dto.*
import org.joda.time.DateTime
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Component
class FlightHandler(private val flightFacade: FlightFacade) {

    companion object {
        private const val FIRST_IATA_PARAM = "firstIata"
        private const val SECOND_IATA_PARAM = "secondIata"
        private const val DESTINATION_IATA_PARAM = "destinationIata"
        private const val TRIP_LENGTH_PARAM = "tripLength"
        private const val OFFSET_PARAM = "offset"
    }

    fun getConnections(request: ServerRequest): Mono<ServerResponse> {
        val firstIata = request.queryParam(FIRST_IATA_PARAM).orElseThrow { RuntimeException("gimme firstIata") }
        val secondIata = request.queryParam(SECOND_IATA_PARAM).orElseThrow { RuntimeException("Gimme secondIata") }
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
        val firstIata = request.queryParam(FIRST_IATA_PARAM).orElseThrow { RuntimeException("Gimme firstIata") }
        val secondIata = request.queryParam(SECOND_IATA_PARAM).orElseThrow { RuntimeException("Gimme secondIata") }
        val destinationIata = request.queryParam(DESTINATION_IATA_PARAM).orElseThrow { RuntimeException("Gimme destinationIata") }
        val offset = request.queryParam(OFFSET_PARAM).orElse("1").toInt()
        val tripLength = request.queryParam(TRIP_LENGTH_PARAM).orElse("5").toInt()

        val routes = flightFacade.findRoutes(firstIata, secondIata, destinationIata, SearchParams(tripLength, offset))
                .map { this.mapToRoutes(it) }

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(
                        routes,
                        RouteResponse::class.java))
    }

    fun findConnectionsAndGetRoutes(request: ServerRequest): Mono<ServerResponse> {
        val firstIata = request.queryParam(FIRST_IATA_PARAM).orElseThrow { RuntimeException("Gimme firstIata") }
        val secondIata = request.queryParam(SECOND_IATA_PARAM).orElseThrow { RuntimeException("Gimme secondIata") }
        val offset = request.queryParam(OFFSET_PARAM).orElse("1").toInt()
        val tripLength = request.queryParam(TRIP_LENGTH_PARAM).orElse("5").toInt()

        val routes: Mono<RouteResponse> = flightFacade.findConnectionsAndGetRoutes(firstIata, secondIata, SearchParams(tripLength, offset))
                .map { routes ->
                    if (routes.firstRoundTrip.flights.isEmpty())
                        return@map RouteResponse(emptyList())
                    val secondFlights = routes.secondRoundTrip.flights
                    val departureAirport = routes.firstRoundTrip.flights[0].departureAirport
                    val secondDepartureAirport = routes.secondRoundTrip.flights[0].departureAirport
                    val mutualArrivalAiport = routes.firstRoundTrip.flights[0].arrivalAirport
                    val flights = routes.firstRoundTrip.flights.mapIndexed { index, roundTripV2 ->
                        val secondTrip = secondFlights[index]
                        RouteDto(
                                RoutePlanDto(
                                        mapToAirportDTO(departureAirport),
                                        mapToAirportDTO(mutualArrivalAiport),
                                        FlightDatesDto(formatDate(roundTripV2.oneWay.date), formatDate(roundTripV2.returnWay.date)),
                                        FlightPriceDto(roundTripV2.oneWay.price!!, roundTripV2.returnWay.price!!, roundTripV2.oneWay.currency)),
                                RoutePlanDto(
                                        mapToAirportDTO(secondDepartureAirport),
                                        mapToAirportDTO(mutualArrivalAiport),
                                        FlightDatesDto(formatDate(secondTrip.oneWay.date), formatDate(secondTrip.returnWay.date)),
                                        FlightPriceDto(secondTrip.oneWay.price!!, secondTrip.returnWay.price!!, secondTrip.oneWay.currency)
                                )

                        )
                    }
                    RouteResponse(flights)
                }.filter { it.routes.isNotEmpty() }
                .toMono()


        return ServerResponse.ok().body(BodyInserters.fromPublisher(routes, RouteResponse::class.java))
    }

    private fun mapToRoutes(routes: Routes): RouteResponse {
        val departureAirport = mapToAirportDTO(routes.firstRoundTrip.departureAirport)
        val secondDepartureAirport = mapToAirportDTO(routes.secondRoundTrip.departureAirport)
        val mutualArrivalAirport = mapToAirportDTO(routes.firstRoundTrip.arrivalAirport)

        val secondFlights = routes.secondRoundTrip.flights
        val routesDtos = routes.firstRoundTrip.flights.mapIndexed { index, roundTrip ->
            val secondTrip = secondFlights[index]
            RouteDto(
                    RoutePlanDto(
                            departureAirport,
                            mutualArrivalAirport,
                            FlightDatesDto(formatDate(roundTrip.oneWay.date), formatDate(roundTrip.returnWay.date)),
                            FlightPriceDto(roundTrip.oneWay.price!!, roundTrip.returnWay.price!!, roundTrip.oneWay.currency)),
                    RoutePlanDto(
                            secondDepartureAirport,
                            mutualArrivalAirport,
                            FlightDatesDto(formatDate(secondTrip.oneWay.date), formatDate(secondTrip.returnWay.date)),
                            FlightPriceDto(secondTrip.oneWay.price!!, secondTrip.returnWay.price!!, secondTrip.oneWay.currency)
                    )

            )
        }
        return RouteResponse(routesDtos)
    }

    private fun formatDate(date: DateTime) = date.toString("dd-MM-yyyy")

    private fun mapToAirportDTO(airport: Airport): AirportPlanDto = AirportPlanDto(airport.iataCode, airport.cityName)


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

data class SearchParams(
        val tripLength: Int,
        val offset: Int
)
