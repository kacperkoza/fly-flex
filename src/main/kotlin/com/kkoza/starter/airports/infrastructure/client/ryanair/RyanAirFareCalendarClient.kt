package com.kkoza.starter.airports.infrastructure.client.ryanair

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.kkoza.starter.airports.FareCalendar
import com.kkoza.starter.airports.FareCalendarClient
import com.kkoza.starter.airports.FlightInfo
import org.joda.time.DateTime
import reactor.core.publisher.Mono

class RyanAirFareCalendarClient(
        private val ryanAirClient: RyanAirClient,
        url: String
) : FareCalendarClient {
    private val path = "$url/farefinder/3/oneWayFares"

    companion object {
        private const val BASE_PARAMS = "/cheapestPerDay?market=pl-pl&outboundMonthOfDate="
    }

    override fun getFareCalendar(departureIata: String, arrivalIata: String): Mono<FareCalendar> {
        val finalPath = "$path/$departureIata/$arrivalIata$BASE_PARAMS${DateTime.now().plusMonths(1).toString("yyyy-MM-dd")}"
        return ryanAirClient
                .requestMono(finalPath,
                        ResponseDto::class.java)
                { calendarDto: ResponseDto -> mapToFareCalendar(departureIata, arrivalIata, calendarDto) }
    }

    private fun mapToFareCalendar(departureIata: String, arrivalIata: String,
                                  calendarDto: ResponseDto): Mono<FareCalendar> {
        val flightInfos = calendarDto.outbound.fares
                .map { FlightInfo(it.day, it.price?.valueMainUnit?.toInt()) }

        return Mono.just(FareCalendar(departureIata, arrivalIata, flightInfos))
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResponseDto(
        val outbound: OutboundDto
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OutboundDto(val fares: List<FareDto>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FareDto(
        @JsonFormat(pattern = "yyyy-MM-dd")
        val day: DateTime,
        val price: PriceDto?)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PriceDto(
        val valueMainUnit: String?
)