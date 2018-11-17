package com.kkoza.starter.airports;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;

@Component
public class AirportsProvider {

    private final ConnectionsProvider connectionsProvider;

    public AirportsProvider(ConnectionsProvider connectionsProvider) {
        this.connectionsProvider = connectionsProvider;
    }

    public Flux<Airport> getConnections(String departure, String destination) {
        return Flux.zip(
                connectionsProvider.getConnections(departure)
                        .collectList(),
                connectionsProvider.getConnections(destination)
                        .collectList()
        ).flatMap(t -> {
            List<Airport> departureConnections = t.getT1();
            List<Airport> destinationConnections = t.getT2();
            return Flux.fromIterable(findCommonAirports(departureConnections, destinationConnections));
        });
    }

    private Collection<Airport> findCommonAirports(List<Airport> departure, List<Airport> destination) {
        return CollectionUtils.intersection(departure, destination);
    }

}
