package com.kkoza.starter.airports

import reactor.core.publisher.Mono

interface FareCalendarClient {

    fun getFareCalendar(departureIata: String, arrivalIata: String): Mono<FareCalendar>

}