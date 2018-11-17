package com.kkoza.starter.airports.infrastructure;

import com.kkoza.starter.airports.Airport;
import reactor.core.publisher.Flux;

public interface AirportClient {

    Flux<Airport> getAllAirports();

    Flux<Airport> getConnections(String departure, String destination);

}
