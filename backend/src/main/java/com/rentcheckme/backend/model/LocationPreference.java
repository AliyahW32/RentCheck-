package com.rentcheckme.backend.model;

public class LocationPreference {
    private String zipCode;
    private boolean useCurrentLocation;
    private String currentLocationLabel;

    public LocationPreference() {
    }

    public LocationPreference(String zipCode, boolean useCurrentLocation, String currentLocationLabel) {
        this.zipCode = zipCode;
        this.useCurrentLocation = useCurrentLocation;
        this.currentLocationLabel = currentLocationLabel;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isUseCurrentLocation() {
        return useCurrentLocation;
    }

    public void setUseCurrentLocation(boolean useCurrentLocation) {
        this.useCurrentLocation = useCurrentLocation;
    }

    public String getCurrentLocationLabel() {
        return currentLocationLabel;
    }

    public void setCurrentLocationLabel(String currentLocationLabel) {
        this.currentLocationLabel = currentLocationLabel;
    }
}
