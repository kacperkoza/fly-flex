package com.kkoza.starter.airports.infrastructure.api

import com.kkoza.starter.BaseIntegrationTest

class AirportRouterTest extends BaseIntegrationTest {

    def 'should return mutual connections for POZ and WMI'() {
        given:
        stubRyanairConnectionsForIata('POZ', 'poz_connections.json')
        stubRyanairConnectionsForIata('WMI', 'wmi_connections.json')

        when:
        ConnectionsResponse response = restTemplate.getForEntity(localUrl("connections?firstIata=POZ&secondIata=WMI"), ConnectionsResponse).body

        then:
        with(response) {
            firstIata == 'POZ'
            secondIata == 'WMI'
            connections.size() == 12
            connections[0].name == 'Ateny'
            connections[0].iataCode == 'ATH'
            connections[1].name == 'Oslo-Torp'
            connections[1].iataCode == 'TRF'
        }
    }

}
