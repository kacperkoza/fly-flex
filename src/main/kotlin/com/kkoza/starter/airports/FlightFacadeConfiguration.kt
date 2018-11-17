package com.kkoza.starter.airports

import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanAirAirportClient
import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanAirClient
import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanAirFareCalendarClient
import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanairConnectionsClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlightFacadeConfiguration {

    @Bean
    fun airportFacade(
            @Value("\${ryanair.url}") ryanAirUrl: String
    ): FlightFacade {
        val ryanAirClient = RyanAirClient()
        val ryanAirAirportClient = RyanAirAirportClient(ryanAirClient, ryanAirUrl)
        val ryanAirConnectionsClient = RyanairConnectionsClient(ryanAirClient, ryanAirUrl)
        val ryanAirFareCalendarClient = RyanAirFareCalendarClient(ryanAirClient, ryanAirUrl)
        val airportsProvider = AirportsProvider(ryanAirConnectionsClient, ryanAirAirportClient)
        val lowestFareCalendarFinder = LowestFareCalendarFinder(ryanAirFareCalendarClient)
        return FlightFacade(airportsProvider, lowestFareCalendarFinder)
    }

}