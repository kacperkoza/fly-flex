package com.kkoza.starter.airports.infrastructure.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RouteResponse(
    val routes: List<Route>
)

data class Route(
        val firstOutbound: RoutePlan,
        val secondOutbound: RoutePlan
)

data class RoutePlan(
        val departureAirport: Airport,
        val arrivalAirport: Airport,
        val date: FlightDates,
        val price: FlightPrice
)

data class Airport(
        val iataCode: String,
        val name: String
)

data class FlightDates(
        val departure: String,
        @JsonProperty("return") val returnWay: String
)

data class FlightPrice(
        val oneWay: Double,
        @JsonProperty("return") val returnWay: Double
)