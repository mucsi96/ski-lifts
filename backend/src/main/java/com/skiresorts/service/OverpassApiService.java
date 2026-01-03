package com.skiresorts.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OverpassApiService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";

    /**
     * Fetch all ski areas in Switzerland
     */
    public List<Map<String, Object>> fetchSwissSkiAreas() {
        String query = """
            [out:json][timeout:60];
            area["ISO3166-1"="CH"]->.switzerland;
            (
              relation["landuse"="winter_sports"](area.switzerland);
              relation["site"="piste"](area.switzerland);
              way["landuse"="winter_sports"](area.switzerland);
            );
            out center tags;
            """;

        return executeQuery(query);
    }

    /**
     * Fetch all ski lifts (aerialways) in Switzerland
     */
    public List<Map<String, Object>> fetchSwissSkiLifts() {
        String query = """
            [out:json][timeout:90];
            area["ISO3166-1"="CH"]->.switzerland;
            (
              way["aerialway"](area.switzerland);
            );
            out geom tags;
            """;

        return executeQuery(query);
    }

    /**
     * Fetch ski lifts for a specific area (by bounding box)
     */
    public List<Map<String, Object>> fetchLiftsInArea(double minLat, double minLon, double maxLat, double maxLon) {
        String query = String.format(Locale.US, """
            [out:json][timeout:60];
            (
              way["aerialway"](%f,%f,%f,%f);
            );
            out geom tags;
            """, minLat, minLon, maxLat, maxLon);

        return executeQuery(query);
    }

    /**
     * Fetch ski lifts near a specific point (within radius in meters)
     */
    public List<Map<String, Object>> fetchLiftsNearPoint(double lat, double lon, int radiusMeters) {
        String query = String.format(Locale.US, """
            [out:json][timeout:60];
            (
              way["aerialway"](around:%d,%f,%f);
            );
            out geom tags;
            """, radiusMeters, lat, lon);

        return executeQuery(query);
    }

    /**
     * Fetch pistes (ski runs) in an area
     */
    public List<Map<String, Object>> fetchPistesInArea(double minLat, double minLon, double maxLat, double maxLon) {
        String query = String.format(Locale.US, """
            [out:json][timeout:60];
            (
              way["piste:type"="downhill"](%f,%f,%f,%f);
              relation["piste:type"="downhill"](%f,%f,%f,%f);
            );
            out geom tags;
            """, minLat, minLon, maxLat, maxLon, minLat, minLon, maxLat, maxLon);

        return executeQuery(query);
    }

    /**
     * Fetch detailed ski area data including lifts and pistes
     */
    public Map<String, Object> fetchSkiAreaDetails(long osmRelationId) {
        String query = String.format("""
            [out:json][timeout:60];
            relation(%d);
            out geom tags;
            (._;>;);
            out geom tags;
            """, osmRelationId);

        List<Map<String, Object>> results = executeQuery(query);

        Map<String, Object> details = new HashMap<>();
        details.put("elements", results);
        return details;
    }

    /**
     * Fetch known Swiss ski resorts by name pattern
     */
    public List<Map<String, Object>> searchSkiResortByName(String name) {
        String query = String.format("""
            [out:json][timeout:30];
            area["ISO3166-1"="CH"]->.switzerland;
            (
              relation["landuse"="winter_sports"]["name"~"%s",i](area.switzerland);
              relation["site"="piste"]["name"~"%s",i](area.switzerland);
              node["sport"="skiing"]["name"~"%s",i](area.switzerland);
            );
            out center tags;
            """, name, name, name);

        return executeQuery(query);
    }

    private List<Map<String, Object>> executeQuery(String query) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(OVERPASS_API_URL)
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                    .build();

            String response = webClient.post()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("data=" + URLEncoder.encode(query, StandardCharsets.UTF_8))
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        log.error("Error executing Overpass query: {}", e.getMessage());
                        return Mono.just("{}");
                    })
                    .block();

            if (response == null || response.isEmpty()) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode elements = root.get("elements");

            if (elements == null || !elements.isArray()) {
                return List.of();
            }

            List<Map<String, Object>> results = new ArrayList<>();
            for (JsonNode element : elements) {
                results.add(objectMapper.convertValue(element, Map.class));
            }

            log.info("Overpass query returned {} elements", results.size());
            return results;

        } catch (Exception e) {
            log.error("Failed to execute Overpass query", e);
            return List.of();
        }
    }

    /**
     * Parse aerialway type from OSM tags
     */
    public String parseAerialwayType(Map<String, Object> tags) {
        if (tags == null) return null;
        Object type = tags.get("aerialway");
        if (type == null) return null;

        return switch (type.toString()) {
            case "cable_car" -> "CABLE_CAR";
            case "gondola" -> "GONDOLA";
            case "chair_lift" -> "CHAIRLIFT";
            case "drag_lift", "platter", "j-bar" -> "DRAG_LIFT";
            case "t-bar" -> "T_BAR";
            case "magic_carpet" -> "MAGIC_CARPET";
            case "funicular" -> "FUNICULAR";
            default -> "CHAIRLIFT";
        };
    }

    /**
     * Parse piste difficulty from OSM tags
     */
    public String parsePisteDifficulty(Map<String, Object> tags) {
        if (tags == null) return null;
        Object difficulty = tags.get("piste:difficulty");
        if (difficulty == null) return null;

        return switch (difficulty.toString()) {
            case "novice", "easy" -> "blue";
            case "intermediate" -> "red";
            case "advanced", "expert", "freeride" -> "black";
            default -> "red";
        };
    }
}
