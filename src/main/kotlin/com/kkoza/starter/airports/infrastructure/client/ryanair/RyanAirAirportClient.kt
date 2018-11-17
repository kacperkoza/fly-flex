package com.kkoza.starter.airports.infrastructure.client.ryanair

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.kkoza.starter.airports.Airport
import com.kkoza.starter.airports.AirportClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class RyanAirAirportClient(
        private val ryanAirClient: RyanAirClient,
        @Value("\${ryanair.url}") url: String
) : AirportClient {

    private val path = "$url/aggregate/4/common?embedded=airports&market=pl-pl"

    override fun getAllAirports(): Flux<Airport> {
        return ryanAirClient.requestFlux(
                path,
                AirportsInfo::class.java
        ) { airportDtoList -> mapAirportDtoToAirport(airportDtoList) }
    }

    private fun mapAirportDtoToAirport(airportsInfo: AirportsInfo): Flux<Airport> {
        val airports = airportsInfo.airports.map { Airport(it.iataCode, it.name) }
        return Flux.fromIterable(airports)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class AirportsInfo(
        @JsonProperty("airports")
        val airports: List<ProviderAirportInfo>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProviderAirportInfo(
        val iataCode: String,
        val name: String)