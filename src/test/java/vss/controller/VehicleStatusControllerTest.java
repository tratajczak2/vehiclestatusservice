package vss.controller;

import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vss.domain.*;
import vss.integration.GarageClient;
import vss.integration.InsuranceClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@MicronautTest
public class VehicleStatusControllerTest {

    @Inject
    private InsuranceClient insuranceClient;

    @Inject
    private GarageClient garageClient;

    @Inject
    private VehicleStatusController vehicleStatusController;

    /**
     * Setup mock responses for the insurance and garage clients.
     */
    @BeforeEach
    void setup() {
        when(insuranceClient.getReport(anyString()))
                .thenReturn(Mono.just(new InsuranceResponse(
                        new InsuranceReport(0))));
        when(garageClient.getReport(anyString()))
                .thenReturn(Mono.just(new GarageResponse("low")));
    }

    @Test
    void testControllerWorksWithAllData() {
        VehicleRequest vehicleRequest = new VehicleRequest("vin", new String[] {"accident_free", "maintenance"});
        Mono<VehicleResponse> vehicleResponse = vehicleStatusController.vehicleStatus(vehicleRequest);

        StepVerifier.create(vehicleResponse)
                .assertNext(vr -> {
                    assertEquals("vin", vr.getVin());
                    assertEquals("poor", vr.getMaintenanceScore());
                    assertEquals(true, vr.getAccidentFree());
                })
                .verifyComplete();
    }

    @Test
    void testControllerWorksWithMaintenanceOnly() {
        VehicleRequest vehicleRequest = new VehicleRequest("vin", new String[] {"maintenance"});
        Mono<VehicleResponse> vehicleResponse = vehicleStatusController.vehicleStatus(vehicleRequest);

        StepVerifier.create(vehicleResponse)
                .assertNext(vr -> {
                    assertEquals("vin", vr.getVin());
                    assertEquals("poor", vr.getMaintenanceScore());
                    Assertions.assertNull(vr.getAccidentFree());
                })
                .verifyComplete();
    }

    @Test
    void testControllerWorksWithAccidentOnly() {
        VehicleRequest vehicleRequest = new VehicleRequest("vin", new String[] {"accident_free"});
        Mono<VehicleResponse> vehicleResponse = vehicleStatusController.vehicleStatus(vehicleRequest);

        StepVerifier.create(vehicleResponse)
                .assertNext(vr -> {
                    assertEquals("vin", vr.getVin());
                    assertEquals(true, vr.getAccidentFree());
                    Assertions.assertNull(vr.getMaintenanceScore());
                })
                .verifyComplete();
    }

    @Test
    void testControllerValidatesVin() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
            () -> {
                VehicleRequest vehicleRequest = new VehicleRequest("", new String[] {"accident_free"});
                Mono<VehicleResponse> vehicleResponse = vehicleStatusController.vehicleStatus(vehicleRequest);
            });
        assertEquals("vehicleStatus.vehicleRequest.vin: must not be blank", exception.getMessage());
    }

    @Test
    void testControllerValidatesFeature() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
            () -> {
                VehicleRequest vehicleRequest = new VehicleRequest("vin", new String[] {});
                Mono<VehicleResponse> vehicleResponse = vehicleStatusController.vehicleStatus(vehicleRequest);
            });
        assertEquals("vehicleStatus.vehicleRequest.features: at least one feature is required", exception.getMessage());
    }

    @MockBean(InsuranceClient.class)
    InsuranceClient mockInsuranceClient() {
        return Mockito.mock(InsuranceClient.class);
    }

    @MockBean(GarageClient.class)
    GarageClient mockGarageClient() {
        return Mockito.mock(GarageClient.class);
    }
}
