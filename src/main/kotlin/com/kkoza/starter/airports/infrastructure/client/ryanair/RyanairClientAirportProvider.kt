package com.kkoza.starter.airports.infrastructure.client.ryanair

import com.kkoza.starter.airports.Airport
import com.kkoza.starter.airports.AirportProvider
import com.kkoza.starter.airports.infrastructure.AirportDtoList
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.net.URI

@Component
class RyanairClientAirportProvider : AirportProvider {

    private val logger = LoggerFactory.getLogger(RyanairClientAirportProvider::class.java)

    private val webClient: WebClient = WebClient.create()

    companion object {
        private const val PATH = "https://api.ryanair.com/aggregate/4/common"
        private const val BASE_PARAMS = "embedded=airports&market=pl-pl"
    }

    override fun getAllAirports(): Flux<Airport> {
        return webClient
                .get()
                .uri { URI("$PATH?$BASE_PARAMS") }
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
                .bodyToMono(AirportDtoList::class.java)
                .toFlux()
                .flatMap { airportDto -> mapAirportDtoToAirport(airportDto) }
    }

    private fun mapAirportDtoToAirport(airportsInfo: AirportDtoList): Flux<Airport> {
        val airports = airportsInfo.airports.map { Airport(it.iataCode, it.name) }
        return Flux.fromIterable(airports)
    }
}