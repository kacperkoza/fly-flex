package com.kkoza.starter.airports

data class Routes(
        val firstRoundTrip: Flights,
        val secondRoundTrip: Flights
)

data class Flights(
        val departureAirport: Airport,
        val arrivalAirport: Airport,
        val flights: List<RoundTrip>
)

data class RoundTrip(
        val oneWay: FlightInfo,
        val returnWay: FlightInfo
)