package com.skiresorts.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutingService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // OSRM public demo server - for production use, host your own instance
    private static final String OSRM_API_URL = "https://router.project-osrm.org";

    // Zurich coordinates (city center)
    private static final double ZURICH_LAT = 47.3769;
    private static final double ZURICH_LON = 8.5417;

    /**
     * Calculate drive time from Zurich to a destination in minutes
     */
    public Integer calculateDriveTimeFromZurich(double destLat, double destLon) {
        return calculateDriveTime(ZURICH_LAT, ZURICH_LON, destLat, destLon);
    }

    /**
     * Calculate drive time between two points in minutes
     */
    public Integer calculateDriveTime(double startLat, double startLon, double endLat, double endLon) {
        try {
            String url = String.format(Locale.US,
                    "%s/route/v1/driving/%f,%f;%f,%f?overview=false",
                    OSRM_API_URL, startLon, startLat, endLon, endLat);

            WebClient webClient = webClientBuilder.build();

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        log.error("Error calculating route: {}", e.getMessage());
                        return Mono.just("{}");
                    })
                    .block();

            if (response == null || response.isEmpty()) {
                return null;
            }

            JsonNode root = objectMapper.readTree(response);
            String status = root.path("code").asText();

            if (!"Ok".equals(status)) {
                log.warn("OSRM returned status: {}", status);
                return null;
            }

            JsonNode routes = root.path("routes");
            if (routes.isArray() && routes.size() > 0) {
                double durationSeconds = routes.get(0).path("duration").asDouble();
                return (int) Math.round(durationSeconds / 60);
            }

            return null;

        } catch (Exception e) {
            log.error("Failed to calculate drive time", e);
            return null;
        }
    }

    /**
     * Calculate distance between two points in meters
     */
    public Integer calculateDistance(double startLat, double startLon, double endLat, double endLon) {
        try {
            String url = String.format(Locale.US,
                    "%s/route/v1/driving/%f,%f;%f,%f?overview=false",
                    OSRM_API_URL, startLon, startLat, endLon, endLat);

            WebClient webClient = webClientBuilder.build();

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) return null;

            JsonNode root = objectMapper.readTree(response);
            JsonNode routes = root.path("routes");

            if (routes.isArray() && routes.size() > 0) {
                return (int) routes.get(0).path("distance").asDouble();
            }

            return null;

        } catch (Exception e) {
            log.error("Failed to calculate distance", e);
            return null;
        }
    }
}
