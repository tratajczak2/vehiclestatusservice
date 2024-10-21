package vss.domain;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class InsuranceReport {
    private Integer claims;

    public InsuranceReport(Integer claims) {
        this.claims = claims;
    }
    public Integer getClaims() {
        return claims;
    }
}
