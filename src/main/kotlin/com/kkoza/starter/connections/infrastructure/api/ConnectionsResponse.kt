package com.kkoza.starter.connections.infrastructure.api

data class ConnectionsResponse(
        val airport: AirportDto,
        val connections: List<AirportDto>
)

data class AirportDto(
        val name: String,
        val iataCode: String
)