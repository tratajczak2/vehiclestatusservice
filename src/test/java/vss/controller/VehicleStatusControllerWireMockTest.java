package vss.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vss.domain.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MicronautTest
@WireMockTest(httpPort = 8888)
public class VehicleStatusControllerWireMockTest {

    @Inject
    VehicleStatusController vehicleStatusController;

    @BeforeAll
    static void setupStubs() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String insuranceBody = objectMapper.writeValueAsString(
                new InsuranceResponse(new InsuranceReport(0)));
        stubFor(get("/accidents/report")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(insuranceBody)));

        String garageBody = objectMapper.writeValueAsString(
                new GarageResponse("low"));
        stubFor(get("/cars/{vin}")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(garageBody)));
    }

    @Test
    void testWithWireMock() {
        VehicleRequest vehicleRequest = new VehicleRequest("vin", new String[] {"maintenance"});
        Mono<VehicleResponse> vehicleResponse = vehicleStatusController.vehicleStatus(vehicleRequest);

        StepVerifier.create(vehicleResponse)
                .assertNext(vr -> {
                    assertEquals("vin", vr.getVin());
                    assertEquals("poor", vr.getMaintenanceScore());
                    assertNull(vr.getAccidentFree());
                })
                .verifyComplete();
    }
}
