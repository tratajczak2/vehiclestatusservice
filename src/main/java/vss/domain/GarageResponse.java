package vss.domain;

public class GarageResponse {
    private String maintenanceFrequency;

    public GarageResponse(String maintenanceFrequency) {
        this.maintenanceFrequency = maintenanceFrequency;
    }
    public String getMaintenanceFrequency() {
        return maintenanceFrequency;
    }
}
