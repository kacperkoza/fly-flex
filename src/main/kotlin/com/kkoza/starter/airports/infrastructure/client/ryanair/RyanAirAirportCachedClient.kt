package com.kkoza.starter.airports.infrastructure.client.ryanair

import com.kkoza.starter.airports.Airport
import com.kkoza.starter.airports.AirportClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import javax.annotation.PostConstruct

@Component
@ConditionalOnProperty("cache.enabled")
@Primary
class RyanAirAirportCachedClient(
        private val ryanAirAirportClient: RyanAirAirportClient
): AirportClient {

    private lateinit var airportByIataCode: Map<String, Airport>

    private lateinit var airports: List<Airport>

    override fun getAllAirports() = Flux.fromIterable(this.airports)

    override fun getByIataCode(iataCode: String): Airport = airportByIataCode.get(iataCode)!!

    @PostConstruct
    fun refresh() {
        val airports = ryanAirAirportClient.getAllAirports()
                .collectList()
                .block()!!
                .toList()

        this.airports = airports
        this.airportByIataCode = airports
                .map { it.iataCode to it }
                .toMap()
    }

}