package com.kkoza.starter.airports;

import com.kkoza.starter.airports.infrastructure.AirportProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class AirportsProvider {

    private final AirportProvider airportProvider;

    public AirportsProvider(AirportProvider airportProvider) {
        this.airportProvider = airportProvider;
    }

    public Flux<Airport> getConnections(String departure, String destination) {
        return airportProvider.getConnections(departure, destination);
    }

}
