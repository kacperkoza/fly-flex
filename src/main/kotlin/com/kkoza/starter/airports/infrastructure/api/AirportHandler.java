package com.kkoza.starter.airports.infrastructure.api;

import com.kkoza.starter.airports.Airport;
import com.kkoza.starter.airports.AirportsProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AirportHandler {

    private static final String DEPARTURE_AIRPORT_PARAM = "departure";
    private static final String DESTINATION_AIRPORT_PARAM = "destination";

    private final AirportsProvider airportsProvider;

    public AirportHandler(AirportsProvider airportsProvider) {
        this.airportsProvider = airportsProvider;
    }

    Mono<ServerResponse> getConnections(ServerRequest request) {
        String departure = getQueryParamFromRequest(request, DEPARTURE_AIRPORT_PARAM); // i put my trust in the client
        String destination = getQueryParamFromRequest(request, DESTINATION_AIRPORT_PARAM);

        Mono<List<AirportDto>> connections = airportsProvider.getConnections(departure, destination)
                .map(this::mapToAirportDto)
                .collectList();

        Mono<ConnectionsResponse> response = connections
                .map(c -> mapToAirportResponse(c, departure, destination));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(response, ConnectionsResponse.class));
    }

    @NotNull
    public Mono<ServerResponse> getAirports(ServerRequest request) {
        Mono<List<AirportDto>> airportsDto = airportsProvider.getAllAirports()
                .map(this::mapToAirportDto)
                .collectList();

        Mono<AirportResponse> airports = airportsDto.map(this::mapToAirportResponse);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(airports, AirportResponse.class));
    }

    @NotNull
    private String getQueryParamFromRequest(ServerRequest request, String departureAirportParam) {
        return request.queryParam(departureAirportParam).get();
    }

    private ConnectionsResponse mapToAirportResponse(List<AirportDto> connections, String departure, String destination) {
        return new ConnectionsResponse(departure, destination, connections);
    }

    private AirportResponse mapToAirportResponse(List<AirportDto> airports) {
        return new AirportResponse(airports);
    }

    private AirportDto mapToAirportDto(Airport airport) {
        return new AirportDto(airport.getCityName(), airport.getIataCode());
    }
}
