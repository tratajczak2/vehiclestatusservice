package vss.integration;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;
import vss.domain.GarageResponse;

/**
 * Abstraction of garage interaction.
 */
@Client(id = "garage-client")
@Header(name = "Content-Type", value = "application/json")
public interface GarageClient {
    @Get("/cars/{vin}")
    Mono<GarageResponse> getReport(@PathVariable String vin);
}
