package com.rentcheckme.backend.repository;

import com.rentcheckme.backend.model.FeeProfile;
import com.rentcheckme.backend.model.Listing;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ListingRepository {

    private final List<Listing> listings = List.of(
        new Listing(1, "South End Studio", "Charlotte, NC", "Studio", "South End", "south-end", 1395, 145, 65, 95, 18, 70, 0, new FeeProfile(75, 35), 1395),
        new Listing(2, "NoDa One-Bedroom", "Charlotte, NC", "1 bed", "NoDa", "noda", 1540, 165, 65, 85, 18, 45, 30, new FeeProfile(100, 40), 1540),
        new Listing(3, "Dilworth Shared 2BR", "Charlotte, NC", "2 bed", "Dilworth", "dilworth", 1925, 210, 70, 60, 22, 65, 0, new FeeProfile(60, 25), 1925),
        new Listing(4, "Midtown Garden Apartment", "Atlanta, GA", "1 bed", "Midtown", "midtown", 1710, 160, 65, 110, 19, 80, 35, new FeeProfile(90, 38), 1710),
        new Listing(5, "Grant Park Shared Loft", "Atlanta, GA", "2 bed", "Grant Park", "grant-park", 2050, 225, 70, 45, 24, 55, 0, new FeeProfile(75, 28), 2050),
        new Listing(6, "Old Fourth Ward Flex", "Atlanta, GA", "1 bed", "Old Fourth Ward", "old-fourth-ward", 1670, 150, 60, 80, 18, 55, 20, new FeeProfile(85, 32), 1670),
        new Listing(7, "Downtown Durham One-Bedroom", "Durham, NC", "1 bed", "Central Park", "central-park", 1485, 145, 60, 85, 18, 35, 25, new FeeProfile(85, 32), 1485),
        new Listing(8, "Brightleaf Split-Level", "Durham, NC", "2 bed", "Brightleaf", "brightleaf", 1820, 195, 60, 35, 22, 40, 0, new FeeProfile(60, 20), 1820)
    );

    public List<Listing> findAll() {
        return listings;
    }
}
