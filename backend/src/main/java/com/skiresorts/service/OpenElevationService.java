package com.skiresorts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenElevationService {

    private final WebClient.Builder webClientBuilder;

    private static final String OPEN_ELEVATION_API = "https://api.open-elevation.com/api/v1/lookup";

    public List<Double> getElevations(List<double[]> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return List.of();
        }

        try {
            List<Map<String, Double>> locations = coordinates.stream()
                    .map(coord -> Map.of("latitude", coord[0], "longitude", coord[1]))
                    .toList();

            Map<String, Object> requestBody = Map.of("locations", locations);

            WebClient webClient = webClientBuilder.baseUrl(OPEN_ELEVATION_API).build();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(e -> {
                        log.error("Error fetching elevation data: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .block();

            if (response != null && response.containsKey("results")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                return results.stream()
                        .map(r -> ((Number) r.get("elevation")).doubleValue())
                        .toList();
            }
        } catch (Exception e) {
            log.error("Failed to fetch elevation data", e);
        }

        return new ArrayList<>();
    }

    public Double getElevation(double latitude, double longitude) {
        List<Double> elevations = getElevations(List.of(new double[]{latitude, longitude}));
        return elevations.isEmpty() ? null : elevations.get(0);
    }

    public List<double[]> interpolatePoints(double startLat, double startLon,
                                            double endLat, double endLon, int numPoints) {
        List<double[]> points = new ArrayList<>();
        for (int i = 0; i < numPoints; i++) {
            double ratio = (double) i / (numPoints - 1);
            double lat = startLat + ratio * (endLat - startLat);
            double lon = startLon + ratio * (endLon - startLon);
            points.add(new double[]{lat, lon});
        }
        return points;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth's radius in meters
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(latRad1) * Math.cos(latRad2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
