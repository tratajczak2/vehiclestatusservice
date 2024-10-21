package vss.domain;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class InsuranceResponse {
    private InsuranceReport report;

    public InsuranceResponse(InsuranceReport report) {
        this.report = report;
    }
    public InsuranceReport getReport() {
        return report;
    }
}
