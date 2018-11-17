package com.kkoza.starter.airports.infrastructure;

import com.kkoza.starter.airports.Airport;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Component
public class RyanairAirportProvider implements AirportProvider {

    @Override
    public Flux<Airport> getAllAirports() {
        List<Airport> airports = new ArrayList<>();
        airports.add(new Airport("a", "b"));
        airports.add(new Airport("a", "b"));
        airports.add(new Airport("a", "b"));
        airports.add(new Airport("a", "b"));
        return Flux.fromIterable(airports);
    }
}
