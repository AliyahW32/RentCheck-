package com.rentcheckme.backend.dto;

import com.rentcheckme.backend.model.Listing;

public class ListingView {
    private long id;
    private String title;
    private String city;
    private String beds;
    private String neighborhood;
    private String areaId;
    private String imageUrl;
    private double rent;
    private double monthlyHidden;
    private double monthlyTotal;
    private double moveInCash;
    private double sharedRent;
    private double leftoverAfterHousing;
    private int shareDivisor;
    private String fitLabel;
    private String fitClassName;

    public ListingView() {
    }

    public ListingView(Listing listing, double monthlyHidden, double monthlyTotal, double moveInCash,
                       double sharedRent, double leftoverAfterHousing, int shareDivisor,
                       String fitLabel, String fitClassName) {
        this.id = listing.getId();
        this.title = listing.getTitle();
        this.city = listing.getCity();
        this.beds = listing.getBeds();
        this.neighborhood = listing.getNeighborhood();
        this.areaId = listing.getAreaId();
        this.imageUrl = listing.getImageUrl();
        this.rent = listing.getRent();
        this.monthlyHidden = monthlyHidden;
        this.monthlyTotal = monthlyTotal;
        this.moveInCash = moveInCash;
        this.sharedRent = sharedRent;
        this.leftoverAfterHousing = leftoverAfterHousing;
        this.shareDivisor = shareDivisor;
        this.fitLabel = fitLabel;
        this.fitClassName = fitClassName;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public String getBeds() {
        return beds;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getRent() {
        return rent;
    }

    public double getMonthlyHidden() {
        return monthlyHidden;
    }

    public double getMonthlyTotal() {
        return monthlyTotal;
    }

    public double getMoveInCash() {
        return moveInCash;
    }

    public double getSharedRent() {
        return sharedRent;
    }

    public double getLeftoverAfterHousing() {
        return leftoverAfterHousing;
    }

    public int getShareDivisor() {
        return shareDivisor;
    }

    public String getFitLabel() {
        return fitLabel;
    }

    public String getFitClassName() {
        return fitClassName;
    }
}
