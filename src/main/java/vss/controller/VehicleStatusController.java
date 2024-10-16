package vss.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import vss.service.VehicleStatusService;
import vss.domain.VehicleRequest;
import vss.domain.VehicleResponse;

import java.util.UUID;

@Controller("/")
public class VehicleStatusController {

    private static final Logger logger
            = LoggerFactory.getLogger(VehicleStatusController.class);

    private final VehicleStatusService vehicleStatusService;

    public VehicleStatusController(VehicleStatusService vehicleStatusService) {
        this.vehicleStatusService = vehicleStatusService;
    }

    @Post("/check")
    public Mono<VehicleResponse> vehicleStatus(@Valid VehicleRequest vehicleRequest) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Request ID: {} received", requestId);
        return vehicleStatusService.vehicleStatus(vehicleRequest, requestId);
    }
}
