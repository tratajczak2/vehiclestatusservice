package vss.domain;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class GarageResponse {
    private String maintenanceFrequency;

    public GarageResponse(String maintenanceFrequency) {
        this.maintenanceFrequency = maintenanceFrequency;
    }
    public String getMaintenanceFrequency() {
        return maintenanceFrequency;
    }

    public void setMaintenanceFrequency(String maintenanceFrequency) {
        this.maintenanceFrequency = maintenanceFrequency;
    }
}
