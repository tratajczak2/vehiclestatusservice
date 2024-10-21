package vss.domain;

import java.io.Serializable;

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
