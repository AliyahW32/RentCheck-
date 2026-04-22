package com.rentcheckme.backend.repository;

import com.rentcheckme.backend.model.CityMap;
import com.rentcheckme.backend.model.MapArea;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class CityMapRepository {

    private final Map<String, CityMap> cityMaps = Map.of(
        "Charlotte, NC", new CityMap("Charlotte, NC", "0 0 420 280", List.of(
            new MapArea("south-end", "South End", "32,178 125,148 156,228 74,252"),
            new MapArea("noda", "NoDa", "198,62 290,36 324,112 238,144"),
            new MapArea("dilworth", "Dilworth", "128,164 215,144 242,220 154,246"),
            new MapArea("uptown", "Uptown", "154,102 226,82 254,142 182,162")
        )),
        "Atlanta, GA", new CityMap("Atlanta, GA", "0 0 420 280", List.of(
            new MapArea("midtown", "Midtown", "128,72 212,48 244,120 162,144"),
            new MapArea("grant-park", "Grant Park", "192,146 294,138 310,234 218,246"),
            new MapArea("old-fourth-ward", "Old Fourth Ward", "224,74 316,64 342,136 254,144"),
            new MapArea("west-midtown", "West Midtown", "76,92 156,74 174,152 92,164")
        )),
        "Durham, NC", new CityMap("Durham, NC", "0 0 420 280", List.of(
            new MapArea("central-park", "Central Park", "164,80 250,62 274,142 188,156"),
            new MapArea("brightleaf", "Brightleaf", "100,110 174,92 192,166 116,182"),
            new MapArea("trinity-park", "Trinity Park", "136,42 212,30 234,88 158,100"),
            new MapArea("downtown", "Downtown", "194,146 282,136 300,212 216,224")
        ))
    );

    public Optional<CityMap> findByCity(String city) {
        return Optional.ofNullable(cityMaps.get(city));
    }

    public Set<String> getSupportedCities() {
        return cityMaps.keySet();
    }
}
