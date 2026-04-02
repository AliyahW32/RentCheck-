package com.rentcheckme.backend.model;

public class FeeProfile {
    private double application;
    private double amenity;

    public FeeProfile() {
    }

    public FeeProfile(double application, double amenity) {
        this.application = application;
        this.amenity = amenity;
    }

    public double getApplication() {
        return application;
    }

    public void setApplication(double application) {
        this.application = application;
    }

    public double getAmenity() {
        return amenity;
    }

    public void setAmenity(double amenity) {
        this.amenity = amenity;
    }
}
