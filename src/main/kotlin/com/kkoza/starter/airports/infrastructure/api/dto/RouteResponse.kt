package com.kkoza.starter.airports.infrastructure.api.dto

data class RouteResponse(
    val routes: List<RouteDto>
)

data class RouteDto(
        val firstOutbound: RoutePlanDto,
        val secondOutbound: RoutePlanDto
)

data class RoutePlanDto(
        val departureAirport: AirportPlanDto,
        val arrivalAirport: AirportPlanDto,
        val date: FlightDatesDto,
        val price: FlightPriceDto
)

data class AirportPlanDto(
        val iataCode: String,
        val name: String
)

data class FlightDatesDto(
        val departure: String,
        val returnWay: String
)

data class FlightPriceDto(
        val oneWay: Double,
        val returnWay: Double,
        val currency: String
)