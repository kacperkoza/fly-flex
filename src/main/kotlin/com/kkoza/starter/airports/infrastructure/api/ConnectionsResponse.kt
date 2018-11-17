package com.kkoza.starter.airports.infrastructure.api

data class ConnectionsResponse(
        val departureIata: String,
        val destinationIata: String,
        val connections: List<AirportDto>
)