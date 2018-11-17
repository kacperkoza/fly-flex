package com.kkoza.starter.airports

import reactor.core.publisher.Flux


class LowestFareCalendarFinder(
        private val fareCalendarClient: FareCalendarClient
) {

    fun findLowestFares(firstIata: String, secondIata: String, destinationIata: String): Flux<*> {
        return Flux.empty<String>()
    }
}