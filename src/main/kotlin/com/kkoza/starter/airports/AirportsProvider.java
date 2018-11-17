package com.kkoza.starter.airports;

import com.kkoza.starter.airports.infrastructure.AirportProvider;
import org.springframework.stereotype.Component;

@Component
public class AirportsProvider {

    private final AirportProvider airportProvider;

    public AirportsProvider(AirportProvider airportProvider) {
        this.airportProvider = airportProvider;
    }
}
