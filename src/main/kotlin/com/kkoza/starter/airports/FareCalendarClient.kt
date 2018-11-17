package com.kkoza.starter.airports

interface FareCalendarClient {

    fun getFareCalendar(departureIata: String, arrivalIata: String): FareCalendar

}