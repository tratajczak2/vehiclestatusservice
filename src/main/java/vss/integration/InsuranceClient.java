package vss.integration;

import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.annotation.Get;
import reactor.core.publisher.Mono;
import vss.domain.InsuranceResponse;

/**
 * Abstraction of insurance interaction.
 */
@Client(id = "insurance-client")
public interface InsuranceClient {
    @Get("/accidents/report")
    Mono<InsuranceResponse> getReport(@QueryValue String vin);
}
