package com.rentcheckme.backend.model;

import java.util.List;

public class CityMap {
    private String city;
    private String viewBox;
    private List<MapArea> areas;

    public CityMap() {
    }

    public CityMap(String city, String viewBox, List<MapArea> areas) {
        this.city = city;
        this.viewBox = viewBox;
        this.areas = areas;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getViewBox() {
        return viewBox;
    }

    public void setViewBox(String viewBox) {
        this.viewBox = viewBox;
    }

    public List<MapArea> getAreas() {
        return areas;
    }

    public void setAreas(List<MapArea> areas) {
        this.areas = areas;
    }
}
