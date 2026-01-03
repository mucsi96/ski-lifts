package com.skiresorts.controller;

import com.skiresorts.model.SkiResort;
import com.skiresorts.model.dto.SkiResortDetailDto;
import com.skiresorts.service.DataImportService;
import com.skiresorts.service.OverpassApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DataImportController {

    private final DataImportService dataImportService;
    private final OverpassApiService overpassApiService;

    /**
     * Import all known Swiss ski resorts from OpenStreetMap
     * This fetches lifts, pistes, and calculates drive times
     */
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> importAllResorts() {
        Map<String, Object> result = dataImportService.importAllResorts();
        return ResponseEntity.ok(result);
    }

    /**
     * Import a single resort by name
     */
    @PostMapping("/resort")
    public ResponseEntity<SkiResort> importResortByName(@RequestParam String name) {
        SkiResort resort = dataImportService.importResortByName(name);
        if (resort == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resort);
    }

    /**
     * Re-import lifts for an existing resort
     */
    @PostMapping("/resort/{id}/lifts")
    public ResponseEntity<SkiResort> importLiftsForResort(@PathVariable Long id) {
        try {
            SkiResort resort = dataImportService.importLiftsForResort(id);
            return ResponseEntity.ok(resort);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Fetch elevation profiles for all lifts of a resort
     */
    @PostMapping("/resort/{id}/elevations")
    public ResponseEntity<SkiResort> fetchElevationProfiles(@PathVariable Long id) {
        try {
            SkiResort resort = dataImportService.fetchElevationProfilesForResort(id);
            return ResponseEntity.ok(resort);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Preview lifts available in OSM for a location
     */
    @GetMapping("/preview/lifts")
    public ResponseEntity<List<Map<String, Object>>> previewLifts(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5000") int radius) {
        List<Map<String, Object>> lifts = overpassApiService.fetchLiftsNearPoint(lat, lon, radius);
        return ResponseEntity.ok(lifts);
    }

    /**
     * Preview pistes available in OSM for an area
     */
    @GetMapping("/preview/pistes")
    public ResponseEntity<List<Map<String, Object>>> previewPistes(
            @RequestParam double minLat,
            @RequestParam double minLon,
            @RequestParam double maxLat,
            @RequestParam double maxLon) {
        List<Map<String, Object>> pistes = overpassApiService.fetchPistesInArea(minLat, minLon, maxLat, maxLon);
        return ResponseEntity.ok(pistes);
    }

    /**
     * Search for ski areas in OSM by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchSkiAreas(@RequestParam String name) {
        List<Map<String, Object>> results = overpassApiService.searchSkiResortByName(name);
        return ResponseEntity.ok(results);
    }
}
