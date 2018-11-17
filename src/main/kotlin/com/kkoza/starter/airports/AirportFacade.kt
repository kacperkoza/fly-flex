package com.kkoza.starter.airports


class AirportFacade(
        private val airportsProvider: AirportsProvider
) {

    fun getAllAirports() = airportsProvider.getAllAirports()

    fun getConnections(firstIata: String, secondIata: String) = airportsProvider.getConnections(firstIata, secondIata)

}