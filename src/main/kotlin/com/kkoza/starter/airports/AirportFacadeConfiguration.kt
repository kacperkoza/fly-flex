package com.kkoza.starter.airports

import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanAirClient
import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanAirAirportClient
import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanairConnectionsClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AirportFacadeConfiguration {

    @Bean
    fun airportFacade(
            @Value("\${ryanair.url}") ryanAirUrl: String
    ): AirportFacade {
        val ryanAirClient = RyanAirClient()
        val ryanAirAirportClient = RyanAirAirportClient(ryanAirClient, ryanAirUrl)
        val ryanAirConnectionsClient = RyanairConnectionsClient(ryanAirClient, ryanAirUrl)
        val airportsProvider = AirportsProvider(ryanAirConnectionsClient, ryanAirAirportClient)
        return AirportFacade(airportsProvider)
    }

}