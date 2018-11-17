package com.kkoza.starter.airports.infrastructure.api

data class ConnectionsResponse(
        val firstIata: String,
        val secondIata: String,
        val connections: List<AirportDto>
)