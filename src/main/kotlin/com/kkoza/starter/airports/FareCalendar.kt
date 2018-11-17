package com.kkoza.starter.airports

import org.joda.time.DateTime

data class FareCalendar(
        val departureAirport: Airport,
        val arrivalAirport: Airport,
        val fares: List<FlightInfo>
)

data class FlightInfo(
        val date: DateTime,
        val price: Int
)