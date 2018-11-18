package com.kkoza.starter.airports

import org.springframework.stereotype.Component


@Component
class FlightFacade(
        private val airportsProvider: AirportsProvider,
        private val lowestFareCalendarFinder: LowestFareCalendarFinder
) {

    fun getAllAirports() = airportsProvider.getAllAirports()

    fun getConnections(firstIata: String, secondIata: String) = airportsProvider.getConnections(firstIata, secondIata)

    fun findRoutes(firstIata: String, secondIata: String, destinationIata: String)
            = lowestFareCalendarFinder.findLowestFares(firstIata, secondIata, destinationIata)

}