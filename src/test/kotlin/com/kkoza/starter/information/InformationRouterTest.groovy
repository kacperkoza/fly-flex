package com.kkoza.starter.information

import com.kkoza.starter.BaseIntegrationTest
import org.springframework.http.MediaType

class InformationRouterTest extends BaseIntegrationTest {

    def 'should return "Hello Wolrd" in body'() {
        expect:
        webTestClient.get().uri('/status/hello')
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo('Hello world!')
    }

    def 'should return bean \'informationEndpoint\' in body'() {
        expect:
        webTestClient.get().uri('status/beans')
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith { it -> it.body.contains('informationEndpoint') }
    }

}
