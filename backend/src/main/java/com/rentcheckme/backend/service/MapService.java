package com.rentcheckme.backend.service;

import com.rentcheckme.backend.model.CityMap;
import com.rentcheckme.backend.repository.CityMapRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapService {

    private final CityMapRepository cityMapRepository;

    public MapService(CityMapRepository cityMapRepository) {
        this.cityMapRepository = cityMapRepository;
    }

    public CityMap getCityMap(String city) {
        return cityMapRepository.findByCity(city).orElseThrow();
    }

    public List<String> areaNames(String city, List<String> areaIds) {
        if (areaIds == null || areaIds.isEmpty()) {
            return List.of();
        }
        return getCityMap(city).getAreas().stream()
            .filter(area -> areaIds.contains(area.getId()))
            .map(area -> area.getName())
            .toList();
    }
}
