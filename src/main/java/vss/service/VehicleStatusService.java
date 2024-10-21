package vss.service;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import vss.domain.GarageResponse;
import vss.domain.InsuranceResponse;
import vss.domain.VehicleRequest;
import vss.domain.VehicleResponse;
import vss.integration.GarageClient;
import vss.integration.InsuranceClient;
import vss.util.GarageToVehicleRequest;
import vss.util.InsuranceToVehicleRequest;

@Singleton
public class VehicleStatusService {

    private static final Logger logger
            = LoggerFactory.getLogger(VehicleStatusService.class);

    private final InsuranceClient insuranceClient;

    private final GarageClient garageClient;

    public VehicleStatusService(InsuranceClient insuranceClient, GarageClient garageClient) {
        this.insuranceClient = insuranceClient;
        this.garageClient = garageClient;
    }

    public Mono<VehicleResponse> vehicleStatus(VehicleRequest vehicleRequest, String requestId) {
        Mono<InsuranceResponse> insuranceResponse = null;
        Mono<GarageResponse> garageResponse = null;

        try {
            /* Call the 3rd party services */
            if (vehicleRequest.isAccidentFreeCheck()) {
                insuranceResponse = insuranceClient.getReport(vehicleRequest.getVin());
            }
            if (vehicleRequest.isMaintenanceCheck()) {
                garageResponse = garageClient.getReport(vehicleRequest.getVin());
            }
            logger.info("Request ID: {} processed 3rd party services", requestId);
        } catch(Exception e) {
            /* Log the error and rethrow the exception */
            logger.info("Request ID: {} failed with 3rd party services: {}", requestId, e.getMessage());
            throw e;
        }

        if (vehicleRequest.isAccidentFreeCheck() && vehicleRequest.isMaintenanceCheck())
            /* Combine the responses */
            return Mono.zip(insuranceResponse, garageResponse, (i, g) ->
                    new VehicleResponse(requestId,
                            vehicleRequest.getVin(),
                            InsuranceToVehicleRequest.fromInsurance(i.getReport().getClaims()),
                            GarageToVehicleRequest.fromGarage(g.getMaintenanceFrequency()))
            ).doOnError(e -> {
                logger.info("Request ID: {} failed to combine responses: {}", requestId, e.getMessage());
            });
        else if (vehicleRequest.isAccidentFreeCheck())
            /* Map the insurance response */
            return insuranceResponse.map(i ->
                    new VehicleResponse(requestId,
                            vehicleRequest.getVin(),
                            InsuranceToVehicleRequest.fromInsurance(i.getReport().getClaims()),
                            null
                    )
            ).doOnError(e -> {
                logger.info("Request ID: {} failed to map insurance response: {}", requestId, e.getMessage());
            });
        else
            /* Map the garage response */
            return garageResponse.map(g ->
                    new VehicleResponse(requestId,
                            vehicleRequest.getVin(),
                            null,
                            GarageToVehicleRequest.fromGarage(g.getMaintenanceFrequency()))
            ).doOnError(e -> {
                logger.info("Request ID: {} failed to map garage response: {}", requestId, e.getMessage());
            });
    }
}
