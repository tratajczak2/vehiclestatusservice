package vss.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vss.domain.*;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WireMockTest(httpPort = VehicleStatusControllerWireMockTest.WIREMOCK_PORT)
public class VehicleStatusControllerWireMockTest implements TestPropertyProvider {
    public static final int WIREMOCK_PORT = 12345;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    VehicleStatusController vehicleStatusController;

    @Test
    void testWithWireMock() {
        String vin = "vin";
        // Consider using lombok's Builder here
        InsuranceResponse insuranceResponse = new InsuranceResponse(new InsuranceReport(0));
        GarageResponse garageResponse = new GarageResponse("low");

        stubInsuranceRequestForVin(vin, insuranceResponse);
        stubGarageRequestForVin(vin, garageResponse);

        VehicleRequest vehicleRequest = new VehicleRequest(vin, new String[] {"maintenance"});
        Mono<VehicleResponse> vehicleResponse = vehicleStatusController.vehicleStatus(vehicleRequest);

        StepVerifier.create(vehicleResponse)
                .assertNext(vr -> {
                    assertEquals("vin", vr.getVin());
                    assertEquals("poor", vr.getMaintenanceScore());
                    assertNull(vr.getAccidentFree());
                })
                .verifyComplete();
    }

    private void stubGarageRequestForVin(String vin, ResponseDefinitionBuilder resp) {
        stubFor(WireMock.get(urlPathTemplate("/cars/{vin}"))
            .withPathParam("vin", equalTo(vin))
            .willReturn(resp));
    }

    private void stubGarageRequestForVin(String vin, GarageResponse garageResponse) {
        stubGarageRequestForVin(vin, aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(toString(garageResponse)));
    }

    private void stubInsuranceRequestForVin(String vin, ResponseDefinitionBuilder resp) {
        stubFor(WireMock.get(urlPathEqualTo("/accidents/report"))
            .withQueryParam("vin", equalTo(vin))
            .willReturn(resp));
    }

    private void stubInsuranceRequestForVin(String vin, InsuranceResponse insuranceResponse) {
        stubInsuranceRequestForVin(vin, aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(toString(insuranceResponse)));
    }

    private String toString(GarageResponse garageResponse) {
        // Instead of relying on the production POJOs to serialize the response, it would be better for the test to
        // construct the JSON response on its own (e.g. by using org.json.JSONObject)
        try {
            return MAPPER.writeValueAsString(garageResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toString(InsuranceResponse insuranceResponse) {
        // Instead of relying on the production POJOs to serialize the response, it would be better for the test to
        // construct the JSON response on its own (e.g. by using org.json.JSONObject)
        try {
            return MAPPER.writeValueAsString(insuranceResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getWireMockBaseUrl() {
        return "http://localhost:" + WIREMOCK_PORT;
    }

    @Override
    public @NonNull Map<String, String> getProperties() {
        return Map.of(
            "micronaut.http.services.garage-client.url", getWireMockBaseUrl(),
            "micronaut.http.services.insurance-client.url", getWireMockBaseUrl());
    }
}
