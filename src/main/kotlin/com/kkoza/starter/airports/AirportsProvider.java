package com.kkoza.starter.airports;

import com.kkoza.starter.airports.infrastructure.AirportClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class AirportsProvider {

    private final AirportClient airportClient;

    public AirportsProvider(AirportClient airportClient) {
        this.airportClient = airportClient;
    }

    public Flux<Airport> getConnections(String departure, String destination) {
        return airportClient.getConnections(departure, destination);
    }

}
