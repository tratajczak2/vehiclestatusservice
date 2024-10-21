package vss.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class VehicleResponse {
    private String requestId;
    private String vin;
    private Boolean accidentFree;
    private String maintenanceScore;

    public VehicleResponse(String requestId, String vin, Boolean accidentFree, String maintenanceScore) {
        this.requestId = requestId;
        this.vin = vin;
        this.accidentFree = accidentFree;
        this.maintenanceScore = maintenanceScore;
    }

    public String getVin() {
        return vin;
    }

    public String getMaintenanceScore() {
        return maintenanceScore;
    }

    public Boolean getAccidentFree() {
        return accidentFree;
    }
}
