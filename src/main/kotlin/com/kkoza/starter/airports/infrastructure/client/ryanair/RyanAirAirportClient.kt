package com.kkoza.starter.airports.infrastructure.client.ryanair

import com.kkoza.starter.airports.Airport
import com.kkoza.starter.airports.AirportClient
import com.kkoza.starter.airports.infrastructure.api.dto.AirportDto
import org.springframework.beans.factory.annotation.Value
import reactor.core.publisher.Flux

class RyanAirAirportClient(
        private val ryanAirClient: RyanAirClient,
        url: String
) : AirportClient {

    private val path = "$url/aggregate/4/common?embedded=airports&market=pl-pl"

    override fun getAllAirports(): Flux<Airport> {
        return ryanAirClient.request(
                path,
                AirportDtoList::class.java
        ) { airportDtoList -> mapAirportDtoToAirport(airportDtoList) }
    }

    private fun mapAirportDtoToAirport(airportsInfo: AirportDtoList): Flux<Airport> {
        val airports = airportsInfo.airports.map { Airport(it.iataCode, it.name) }
        return Flux.fromIterable(airports)
    }
}

data class AirportDtoList(val airports: List<AirportDto>)