package com.rentcheckme.backend.model;

public class Listing {
    private long id;
    private String title;
    private String city;
    private String beds;
    private String neighborhood;
    private String areaId;
    private String imageUrl;
    private double rent;
    private double utilities;
    private double internet;
    private double parking;
    private double insurance;
    private double transit;
    private double pet;
    private FeeProfile fees;
    private double deposit;

    public Listing() {
    }

    public Listing(long id, String title, String city, String beds, String neighborhood, String areaId, String imageUrl, double rent,
                   double utilities, double internet, double parking, double insurance, double transit, double pet,
                   FeeProfile fees, double deposit) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.beds = beds;
        this.neighborhood = neighborhood;
        this.areaId = areaId;
        this.imageUrl = imageUrl;
        this.rent = rent;
        this.utilities = utilities;
        this.internet = internet;
        this.parking = parking;
        this.insurance = insurance;
        this.transit = transit;
        this.pet = pet;
        this.fees = fees;
        this.deposit = deposit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRent() {
        return rent;
    }

    public void setRent(double rent) {
        this.rent = rent;
    }

    public double getUtilities() {
        return utilities;
    }

    public void setUtilities(double utilities) {
        this.utilities = utilities;
    }

    public double getInternet() {
        return internet;
    }

    public void setInternet(double internet) {
        this.internet = internet;
    }

    public double getParking() {
        return parking;
    }

    public void setParking(double parking) {
        this.parking = parking;
    }

    public double getInsurance() {
        return insurance;
    }

    public void setInsurance(double insurance) {
        this.insurance = insurance;
    }

    public double getTransit() {
        return transit;
    }

    public void setTransit(double transit) {
        this.transit = transit;
    }

    public double getPet() {
        return pet;
    }

    public void setPet(double pet) {
        this.pet = pet;
    }

    public FeeProfile getFees() {
        return fees;
    }

    public void setFees(FeeProfile fees) {
        this.fees = fees;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }
}
