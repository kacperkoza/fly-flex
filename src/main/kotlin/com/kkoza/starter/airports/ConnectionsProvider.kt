package com.kkoza.starter.airports

import reactor.core.publisher.Flux

interface ConnectionsProvider {

    fun getConnections(cityIata: String): Flux<Airport>

}