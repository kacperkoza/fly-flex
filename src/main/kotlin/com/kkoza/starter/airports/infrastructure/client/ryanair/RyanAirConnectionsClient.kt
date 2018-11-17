package com.kkoza.starter.airports.infrastructure.client.ryanair

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.kkoza.starter.airports.Airport
import com.kkoza.starter.airports.AirportConnectionsClient
import reactor.core.publisher.Flux

class RyanairConnectionsClient(
        private val ryanAirClient: RyanAirClient,
        url: String
) : AirportConnectionsClient {

    private val path = "$url/farefinder/3/oneWayFares"

    companion object {
        private const val DEPARTURE_AIRPORT_IATA_CODE = "departureAirportIataCode"
        private const val BASE_PARAMS = "language=pl&market=pl-pl&offset=0&outboundDepartureDateFrom=2018-10-04&outboundDepartureDateTo=2019-10-26&priceValueTo=150"
    }

    override fun getConnections(iataCode: String): Flux<Airport> {
        return ryanAirClient.request(
                "$path?$DEPARTURE_AIRPORT_IATA_CODE=$iataCode&$BASE_PARAMS",
                ConnectionsDto::class.java
        ) { connectionsDto: ConnectionsDto -> mapToAirports(connectionsDto) }
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