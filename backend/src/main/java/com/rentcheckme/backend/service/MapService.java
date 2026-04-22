package com.rentcheckme.backend.service;

import com.rentcheckme.backend.model.CityMap;
import com.rentcheckme.backend.repository.CityMapRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MapService {

    private final CityMapRepository cityMapRepository;
    private final Map<String, String> aliases = new LinkedHashMap<>();

    public MapService(CityMapRepository cityMapRepository) {
        this.cityMapRepository = cityMapRepository;
        aliases.put("charlotte", "Charlotte, NC");
        aliases.put("charlotte nc", "Charlotte, NC");
        aliases.put("atlanta", "Atlanta, GA");
        aliases.put("atlanta ga", "Atlanta, GA");
        aliases.put("durham", "Durham, NC");
        aliases.put("durham nc", "Durham, NC");
    }

    public CityMap getCityMap(String city) {
        return cityMapRepository.findByCity(normalizeCity(city)).orElseThrow();
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

    public String normalizeCity(String city) {
        if (city == null || city.isBlank()) {
            return cityMapRepository.getSupportedCities().stream().findFirst().orElse("Charlotte, NC");
        }

        String trimmed = city.trim();
        if (cityMapRepository.findByCity(trimmed).isPresent()) {
            return trimmed;
        }

        String key = trimmed.toLowerCase().replace(",", "");
        return aliases.getOrDefault(key, trimmed);
    }

    public List<String> getSupportedCities() {
        return new ArrayList<>(cityMapRepository.getSupportedCities());
    }
}
