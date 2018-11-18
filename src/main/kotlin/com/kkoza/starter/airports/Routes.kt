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



///////// V2

data class RoutesV2(
        val firstRoundTrip: FlightsV2,
        val secondRoundTrip: FlightsV2
)

data class FlightsV2(
        val flights: List<RoundTripV2>
)

data class RoundTripV2(
        val departureAirport: Airport,
        val arrivalAirport: Airport,
        val oneWay: FlightInfo,
        val returnWay: FlightInfo
)