package com.rentcheckme.backend.repository;

import com.rentcheckme.backend.model.FeeProfile;
import com.rentcheckme.backend.model.Listing;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ListingRepository {

    private final List<Listing> listings = List.of(
        new Listing(1, "South End Studio", "Charlotte, NC", "Studio", "South End", "south-end", "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80", 1395, 145, 65, 95, 18, 70, 0, new FeeProfile(75, 35), 1395),
        new Listing(2, "NoDa One-Bedroom", "Charlotte, NC", "1 bed", "NoDa", "noda", "https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=900&q=80", 1540, 165, 65, 85, 18, 45, 30, new FeeProfile(100, 40), 1540),
        new Listing(3, "Dilworth Shared 2BR", "Charlotte, NC", "2 bed", "Dilworth", "dilworth", "https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=900&q=80", 1925, 210, 70, 60, 22, 65, 0, new FeeProfile(60, 25), 1925),
        new Listing(4, "Midtown Garden Apartment", "Atlanta, GA", "1 bed", "Midtown", "midtown", "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=900&q=80", 1710, 160, 65, 110, 19, 80, 35, new FeeProfile(90, 38), 1710),
        new Listing(5, "Grant Park Shared Loft", "Atlanta, GA", "2 bed", "Grant Park", "grant-park", "https://images.unsplash.com/photo-1460317442991-0ec209397118?auto=format&fit=crop&w=900&q=80", 2050, 225, 70, 45, 24, 55, 0, new FeeProfile(75, 28), 2050),
        new Listing(6, "Old Fourth Ward Flex", "Atlanta, GA", "1 bed", "Old Fourth Ward", "old-fourth-ward", "https://images.unsplash.com/photo-1448630360428-65456885c650?auto=format&fit=crop&w=900&q=80", 1670, 150, 60, 80, 18, 55, 20, new FeeProfile(85, 32), 1670),
        new Listing(7, "Downtown Durham One-Bedroom", "Durham, NC", "1 bed", "Central Park", "central-park", "https://images.unsplash.com/photo-1464890100898-a385f744067f?auto=format&fit=crop&w=900&q=80", 1485, 145, 60, 85, 18, 35, 25, new FeeProfile(85, 32), 1485),
        new Listing(8, "Brightleaf Split-Level", "Durham, NC", "2 bed", "Brightleaf", "brightleaf", "https://images.unsplash.com/photo-1416331108676-a22ccb276e35?auto=format&fit=crop&w=900&q=80", 1820, 195, 60, 35, 22, 40, 0, new FeeProfile(60, 20), 1820)
    );

    public List<Listing> findAll() {
        return listings;
    }
}
