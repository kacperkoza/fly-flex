package com.kkoza.starter.airports

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.stream.Collectors


class LowestFareCalendarFinder(
        private val fareCalendarClient: FareCalendarClient
) {

    companion object {
        private const val LENGTH_OF_STAY_IN_DAYS = 5
        private const val LIMIT_OF_ROUTES = 10L
    }

    fun findLowestFares(firstIata: String, secondIata: String, destinationIata: String): Mono<Routes> {
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
            getCheapestFlights(firstOneWayCalendar, firstReturnCalendar, secondOneWayCalendar, secondReturnCalendar)
        }.toMono()

    }

    private fun getCheapestFlights(firstCalendar: FareCalendar,
                                   secondCalendar: FareCalendar,
                                   secondOneWayCalendar: FareCalendar,
                                   secondReturnCalendar: FareCalendar
    ): Mono<Routes> {
        val faresOneWayA = firstCalendar.fares
        val faresReturnA = secondCalendar.fares
        val faresOneWayB = secondOneWayCalendar.fares
        val faresReturnB = secondReturnCalendar.fares

        if (faresOneWayA.size != faresReturnA.size || faresOneWayB.size != faresReturnB.size)
            throw IllegalStateException("Kamil miałeś niczego nie filtrować")

        val iterations = faresOneWayA.size - LENGTH_OF_STAY_IN_DAYS

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

    private fun getAirport(iataCode: String) = AirportsCache.AIRPORTS_MAP[iataCode]!!

    private fun toRoundTrip(oneWay: FlightInfo, returnWay: FlightInfo): RoundTrip {
        return RoundTrip(oneWay, returnWay)
    }

    private data class FlightPairHolder(
            val oneWayA: FlightInfo,
            val returnA: FlightInfo,
            val oneWayB: FlightInfo,
            val returnB: FlightInfo
    ) {
//        init {
//            if (oneWayA.price == null || returnA.price == null || oneWayB.price == null || returnB.price == null)
//                throw IllegalStateException("Price in FlightPairHolder cannot be null")
//        }

        fun calculateTripCost() = oneWayA.price!! + oneWayB.price!! + returnA.price!! + returnB.price!!
    }

}