package com.kkoza.starter.airports;

import reactor.core.publisher.Flux;

public interface AirportProvider {
    Flux<Airport> getAllAirports();
}
