package com.kkoza.starter.airports

import reactor.core.publisher.Flux

interface AirportClient {

    fun getAllAirports(): Flux<Airport>

}
