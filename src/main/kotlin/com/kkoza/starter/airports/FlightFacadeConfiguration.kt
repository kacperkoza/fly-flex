package com.kkoza.starter.airports

import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanAirAirportClient
import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanAirClient
import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanairConnectionsClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
class FlightFacadeConfiguration {

    @Bean
    fun airportFacade(
            @Value("\${ryanair.url}") ryanAirUrl: String
    ): FlightFacade {
        val ryanAirClient = RyanAirClient()
        val ryanAirAirportClient = RyanAirAirportClient(ryanAirClient, ryanAirUrl)
        val ryanAirConnectionsClient = RyanairConnectionsClient(ryanAirClient, ryanAirUrl)
        val airportsProvider = AirportsProvider(ryanAirConnectionsClient, ryanAirAirportClient)
        val lowestFareCalendarFinder = LowestFareCalendarFinder(object: FareCalendarClient {
            override fun getFareCalendar(departureIata: String, arrivalIata: String): Mono<FareCalendar> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
        return FlightFacade(airportsProvider, lowestFareCalendarFinder)
    }

}