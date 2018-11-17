package com.kkoza.starter.airports.infrastructure.client.ryanair

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.kkoza.starter.airports.Airport
import com.kkoza.starter.airports.ConnectionsProvider
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.net.URI

@Component
class ConnectionsClient : ConnectionsProvider {

    private val logger = LoggerFactory.getLogger(ConnectionsClient::class.java)

    private val webClient: WebClient = WebClient.create()

    companion object {
        private const val PATH = "https://api.ryanair.com/farefinder/3/oneWayFares"
        private const val DEPARTURE_AIRPORT_IATA_CODE = "departureAirportIataCode"
        private const val BASE_PARAMS = "language=pl&market=pl-pl&offset=0&outboundDepartureDateFrom=2018-10-04&outboundDepartureDateTo=2019-10-26&priceValueTo=150"
    }

    override fun getConnections(cityIata: String): Flux<Airport> {
        return webClient
                .get()
                .uri { URI("$PATH?$DEPARTURE_AIRPORT_IATA_CODE=$cityIata&$BASE_PARAMS") }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError) { clientResponse ->
                    logger.error("Connections client error")
                    throw ConnectionsClientException("Status ${clientResponse.statusCode().value()}")
                }
                .onStatus(HttpStatus::is5xxServerError) { clientResponse ->
                    logger.error("Connections client error")
                    throw ConnectionsClientException("Status ${clientResponse.statusCode().value()}")
                }
                .bodyToMono(ConnectionsDto::class.java)
                .toFlux()
                .flatMap { connectionsDto -> mapToAirports(connectionsDto) }

    }

    private fun mapToAirports(connectionsDto: ConnectionsDto): Flux<Airport> {
        val airports = connectionsDto.fares.map {
            val airport = it.outbound.arrivalAirport
            Airport(airport.iataCode, airport.name)
        }
        return Flux.fromIterable(airports)
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConnectionsDto(
        val fares: List<Outbound>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Outbound(
        val outbound: OutboundInfo
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OutboundInfo(
        val arrivalAirport: AirportInfo
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AirportInfo(
        val iataCode: String,
        val name: String,
        val countryName: String
)

class ConnectionsClientException(message: String) : RuntimeException(message)

