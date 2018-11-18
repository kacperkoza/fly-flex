package com.kkoza.starter.airports

import com.kkoza.starter.airports.infrastructure.api.SearchParams
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Component
class FlightFacade(
        private val airportsProvider: AirportsProvider,
        private val lowestFareCalendarFinder: LowestFareCalendarFinder
) {

    fun getAllAirports() = airportsProvider.getAllAirports()

    fun getConnections(firstIata: String, secondIata: String) = airportsProvider.getConnections(firstIata, secondIata)

    fun findRoutes(firstIata: String, secondIata: String, destinationIata: String, searchParams: SearchParams)
            = lowestFareCalendarFinder.findLowestFares(firstIata, secondIata, destinationIata, searchParams)

    fun findConnectionsAndGetRoutes(firstIata: String, secondIata: String, searchParams: SearchParams): Flux<RoutesV2> {
        return lowestFareCalendarFinder.findLowestFaresV2(firstIata, secondIata, searchParams)
    }

}