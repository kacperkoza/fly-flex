package com.kkoza.starter.airports

import org.apache.commons.collections4.CollectionUtils
import reactor.core.publisher.Flux

class AirportsProvider(private val airportConnectionsClient: AirportConnectionsClient,
                       private val airportClient: AirportClient) {

    fun getAllAirports() = airportClient.getAllAirports()

    fun getConnections(firstIata: String, secondIata: String): Flux<Airport> {
        return Flux.zip(
                airportConnectionsClient.getConnections(firstIata).collectList(),
                airportConnectionsClient.getConnections(secondIata).collectList()
        ).flatMap { t ->
            val departureConnections = t.t1
            val destinationConnections = t.t2
            Flux.fromIterable(findCommonAirports(departureConnections, destinationConnections))
        }
    }

    private fun findCommonAirports(departure: List<Airport>, destination: List<Airport>): Collection<Airport> {
        return CollectionUtils.intersection(departure, destination)
    }
}
