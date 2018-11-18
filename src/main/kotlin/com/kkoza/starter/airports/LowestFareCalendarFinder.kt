package com.kkoza.starter.airports

import com.kkoza.starter.airports.infrastructure.api.SearchParams
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.stream.Collectors


@Component
class LowestFareCalendarFinder(
        private val fareCalendarClient: FareCalendarClient,
        private val airportClient: AirportClient
) {

    companion object {
        private const val LIMIT_OF_ROUTES = 10L
    }

    fun findLowestFares(firstIata: String, secondIata: String, destinationIata: String, searchParams: SearchParams): Mono<Routes> {
        return Flux.zip(
                fareCalendarClient.getFareCalendar(firstIata, destinationIata),
                fareCalendarClient.getFareCalendar(destinationIata, firstIata),
                fareCalendarClient.getFareCalendar(secondIata, destinationIata),
                fareCalendarClient.getFareCalendar(destinationIata, secondIata)
        ).flatMap {
            val firstOneWayCalendar = it.t1
            val firstReturnCalendar = it.t2
            val secondOneWayCalendar = it.t3
            val secondReturnCalendar = it.t4
            getCheapestFlights(firstOneWayCalendar, firstReturnCalendar, secondOneWayCalendar, secondReturnCalendar, searchParams)
        }.toMono()

    }

    private fun getCheapestFlights(firstCalendar: FareCalendar,
                                   secondCalendar: FareCalendar,
                                   secondOneWayCalendar: FareCalendar,
                                   secondReturnCalendar: FareCalendar,
                                   searchParams: SearchParams
    ): Mono<Routes> {
        val tripLength = searchParams.tripLength - searchParams.offset
        val offset = searchParams.offset

        val faresOneWayA = firstCalendar.fares
        val faresReturnA = secondCalendar.fares
        val faresOneWayB = secondOneWayCalendar.fares
        val faresReturnB = secondReturnCalendar.fares
        checkSizeSame(faresOneWayA, faresReturnA, faresOneWayB, faresReturnB)
        val iterations = faresOneWayA.size - tripLength - offset

        val flightsHolder = mutableListOf<FlightPairHolder>()

        for (i in 0 until iterations) {
            val oneWayA = faresOneWayA[i]
            val returnA = faresReturnA[i + LENGTH_OF_STAY_IN_DAYS]
            val oneWayB = faresOneWayB[i]
            val returnB = faresReturnB[i + LENGTH_OF_STAY_IN_DAYS]
            if (oneWayA.price == null || returnA.price == null || oneWayB.price == null || returnB.price == null)
                continue
            flightsHolder.add(FlightPairHolder(oneWayA, returnA, oneWayB, returnB))
        }
        val flights = flightsHolder.stream()
                .sorted { o1, o2 -> o1.calculateTripCost() - o2.calculateTripCost() }
                .limit(LIMIT_OF_ROUTES)
                .collect(Collectors.toList())

        return Mono.just(
                Routes(
                        Flights(
                                getAirport(firstCalendar.departureAirportIata),
                                getAirport(firstCalendar.arrivalAirportIata),
                                flights.map { toRoundTrip(it.oneWayA, it.returnA) })
                        ,
                        Flights(
                                getAirport(secondOneWayCalendar.departureAirportIata),
                                getAirport(secondOneWayCalendar.arrivalAirportIata),
                                flights.map { toRoundTrip(it.oneWayB, it.returnB) }
                        )

                )
        )
    }

    private fun toRoundTrip(oneWay: FlightInfo, returnWay: FlightInfo): RoundTrip {
        return RoundTrip(oneWay, returnWay)
    }

}

private data class FlightPairHolder(
        val oneWayA: FlightInfo,
        val returnA: FlightInfo,
        val oneWayB: FlightInfo,
        val returnB: FlightInfo
) {
    fun calculateTripCost() = oneWayA.price!! + oneWayB.price!! + returnA.price!! + returnB.price!!
}