package com.kkoza.starter.airports

import com.kkoza.starter.airports.infrastructure.api.dto.AirportDto
import com.kkoza.starter.airports.infrastructure.client.ryanair.RyanAirAirportClient
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class AirportsCache(val ryanAirAirportClient: RyanAirAirportClient) {

    companion object {
        val AIRPORTS_MAP: HashMap<String, Airport> = HashMap()
    }

    @PostConstruct
    fun getAirportsMap() {
        ryanAirAirportClient.getAllAirports()
                .map { AirportDto(it.cityName, it.iataCode) }
                .collectList()
                .block()!!
                .forEach {
                    AIRPORTS_MAP[it.iataCode] = Airport(it.iataCode, it.name)
                }
    }
}