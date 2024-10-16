package vss.util;

public class GarageToVehicleRequest {

    public static String fromGarage(String maintenanceFrequency) {
        if (maintenanceFrequency.equals("very_low") || maintenanceFrequency.equals("low")) {
            return "poor";
        } else if (maintenanceFrequency.equals("medium")) {
            return "average";
        } else if (maintenanceFrequency.equals("high")) {
            return "good";
        } else {
            return "unknown";
        }
    }
}
