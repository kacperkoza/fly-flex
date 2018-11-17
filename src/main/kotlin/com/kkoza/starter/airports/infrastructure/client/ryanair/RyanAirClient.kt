package com.kkoza.starter.airports.infrastructure.client.ryanair

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.net.URI

@Component
class RyanAirClient {

    private val logger = LoggerFactory.getLogger(RyanAirClient::class.java)

    private val webClient: WebClient = WebClient.create()

    fun <T : Any, R> request(path: String, bodyType: Class<T>, mapper: (T) -> Flux<R>): Flux<R> {
        return webClient
                .get()
                .uri { URI(path) }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError) { clientResponse ->
                    logger.error("Connections client error")
                    throw ConnectionsClientException("Status ${clientResponse.statusCode().value()}")
                }
                .onStatus(HttpStatus::is5xxServerError) { clientResponse ->
                    logger.error("Connections client error")
                    throw ConnectionsClientException("Status ${clientResponse.statusCode().value()}")
                }
                .bodyToMono(bodyType)
                .toFlux()
                .flatMap { responseBody -> mapper.invoke(responseBody) }
    }

}

class ConnectionsClientException(message: String) : RuntimeException(message)