package com.kkoza.starter.airports

import org.joda.time.DateTime

data class FareCalendar(
        val departureAirportIata: String,
        val arrivalAirportIata: String,
        val fares: List<FlightInfo>
)

data class FlightInfo(
        val date: DateTime,
        val price: Double?,
        val currency: String
)