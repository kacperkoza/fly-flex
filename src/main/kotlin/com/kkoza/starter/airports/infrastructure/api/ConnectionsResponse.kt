package com.kkoza.starter.airports.infrastructure.api

data class ConnectionsResponse(
        val departureIata: String,
        val destinationIata: String,
        val connections: List<AirportDto>
)

data class AirportDto(
        val name: String,
        val iataCode: String
)