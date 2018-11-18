package com.kkoza.starter

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["integration"])
class BaseIntegrationTest extends Specification {

    @Autowired
    WebTestClient webTestClient

    RestTemplate restTemplate = new RestTemplate()

    @Rule
    public WireMockRule ryanairRule = new WireMockRule(8089)

    @LocalServerPort
    int port

    def localUrl(String path) {
        "http://localhost:$port/$path"
    }

    def stubRyanairConnectionsForIata(String iataCode, String bodyFile) {
        ryanairRule.stubFor(
                get(urlPathMatching('/farefinder/3/oneWayFares'))
                        .withQueryParam('departureAirportIataCode', equalTo(iataCode))
                        .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile("ryanair/$bodyFile"))
        )
    }

    def stubRyanairAirports(String bodyFile) {
        ryanairRule.stubFor(
                get(urlPathMatching('/aggregate/4/common'))
                        .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile("ryanair/$bodyFile")
                )
        )
    }

}