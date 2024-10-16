package vss.domain;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Introspected
public class VehicleRequest {
    @NotNull(message = "VIN is required")
    @NotBlank
    private String vin;
    @Size(min=1, message = "at least one feature is required")
    private String [] features;

    public VehicleRequest(String vin, String [] features) {
        this.vin = vin;
        this.features = features;
    }

    public String getVin() {
        return vin;
    }

    public boolean isMaintenanceCheck() {
        for (String feature : features) {
            if (feature.equals("maintenance")) {
                return true;
            }
        }
        return false;
    }

    public boolean isAccidentFreeCheck() {
        for (String feature : features) {
            if (feature.equals("accident_free")) {
                return true;
            }
        }
        return false;
    }

    public String[] getFeatures() {
        return features;
    }
}
