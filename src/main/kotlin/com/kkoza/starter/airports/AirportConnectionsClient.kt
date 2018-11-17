package com.kkoza.starter.airports

import reactor.core.publisher.Flux

interface AirportConnectionsClient {

    fun getConnections(iataCode: String): Flux<Airport>

}