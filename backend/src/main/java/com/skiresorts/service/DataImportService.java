package com.skiresorts.service;

import com.skiresorts.model.ElevationPoint;
import com.skiresorts.model.Lift;
import com.skiresorts.model.LiftType;
import com.skiresorts.model.SkiResort;
import com.skiresorts.repository.LiftRepository;
import com.skiresorts.repository.SkiResortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataImportService {

    private final OverpassApiService overpassApiService;
    private final RoutingService routingService;
    private final OpenElevationService openElevationService;
    private final SkiResortRepository skiResortRepository;
    private final LiftRepository liftRepository;

    // Well-known Swiss ski resorts with coordinates centered on ski areas (not villages)
    // Using larger search radii to capture all lifts in spread-out ski domains
    private static final List<ResortInfo> KNOWN_RESORTS = List.of(
            new ResortInfo("Zermatt", "Valais", "VS", 46.0107, 7.7700, 10000),  // Centered between village and Klein Matterhorn
            new ResortInfo("Arosa Lenzerheide", "Graubünden", "GR", 46.7900, 9.6700, 12000),  // Large connected area
            new ResortInfo("St. Moritz", "Engadin", "GR", 46.5000, 9.8500, 10000),
            new ResortInfo("Verbier", "Valais", "VS", 46.1000, 7.2300, 10000),  // 4 Vallées area
            new ResortInfo("Davos Klosters", "Graubünden", "GR", 46.8100, 9.8500, 12000),  // Multiple ski areas
            new ResortInfo("Laax", "Graubünden", "GR", 46.8400, 9.2300, 10000),  // Flims-Laax-Falera
            new ResortInfo("Engelberg-Titlis", "Central Switzerland", "OW", 46.8000, 8.4100, 8000),
            new ResortInfo("Grindelwald", "Bernese Oberland", "BE", 46.6300, 8.0200, 10000),  // Jungfrau region
            new ResortInfo("Wengen", "Bernese Oberland", "BE", 46.6100, 7.9300, 8000),
            new ResortInfo("Saas-Fee", "Valais", "VS", 46.1100, 7.9300, 6000),
            new ResortInfo("Crans-Montana", "Valais", "VS", 46.3200, 7.5000, 8000),
            new ResortInfo("Adelboden", "Bernese Oberland", "BE", 46.5000, 7.5600, 8000),  // Adelboden-Lenk area
            new ResortInfo("Lenk", "Bernese Oberland", "BE", 46.4600, 7.4400, 6000),
            new ResortInfo("Gstaad", "Bernese Oberland", "BE", 46.4800, 7.2900, 10000),  // Large ski area
            new ResortInfo("Flims", "Graubünden", "GR", 46.8400, 9.2800, 8000),
            new ResortInfo("Andermatt", "Central Switzerland", "UR", 46.6400, 8.6000, 8000),  // SkiArena Andermatt-Sedrun
            new ResortInfo("Villars", "Vaud", "VD", 46.3000, 7.0600, 6000),
            new ResortInfo("Champéry", "Valais", "VS", 46.1800, 6.8700, 6000),  // Portes du Soleil
            new ResortInfo("Nendaz", "Valais", "VS", 46.1900, 7.3100, 8000),  // 4 Vallées
            new ResortInfo("Leukerbad", "Valais", "VS", 46.3800, 7.6300, 5000)
    );

    record ResortInfo(String name, String region, String canton, double lat, double lon, int searchRadius) {}

    /**
     * Import all known Swiss ski resorts with their lifts
     */
    @Transactional
    public Map<String, Object> importAllResorts() {
        log.info("Starting import of Swiss ski resorts...");

        List<SkiResort> importedResorts = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (ResortInfo info : KNOWN_RESORTS) {
            try {
                log.info("Importing resort: {}", info.name());
                SkiResort resort = importResort(info);
                if (resort != null) {
                    importedResorts.add(resort);
                    log.info("Successfully imported {} with {} lifts", info.name(), resort.getLifts().size());
                }
            } catch (Exception e) {
                String error = "Failed to import " + info.name() + ": " + e.getMessage();
                log.error(error, e);
                errors.add(error);
            }

            // Rate limiting - be nice to the public APIs
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("importedCount", importedResorts.size());
        result.put("resorts", importedResorts.stream().map(SkiResort::getName).toList());
        result.put("errors", errors);
        return result;
    }

    /**
     * Import a single resort by name
     */
    @Transactional
    public SkiResort importResortByName(String name) {
        return KNOWN_RESORTS.stream()
                .filter(r -> r.name().equalsIgnoreCase(name))
                .findFirst()
                .map(this::importResort)
                .orElse(null);
    }

    /**
     * Import lifts for an existing resort
     */
    @Transactional
    public SkiResort importLiftsForResort(Long resortId) {
        SkiResort resort = skiResortRepository.findById(resortId)
                .orElseThrow(() -> new RuntimeException("Resort not found"));

        if (resort.getLatitude() == null || resort.getLongitude() == null) {
            throw new RuntimeException("Resort coordinates not available");
        }

        // Clear existing lifts
        resort.getLifts().clear();
        skiResortRepository.save(resort);

        // Fetch lifts from OSM
        List<Map<String, Object>> osmLifts = overpassApiService.fetchLiftsNearPoint(
                resort.getLatitude(), resort.getLongitude(), 8000);

        List<Lift> lifts = parseLifts(osmLifts, resort);
        resort.getLifts().addAll(lifts);

        return skiResortRepository.save(resort);
    }

    /**
     * Fetch elevation profiles for all lifts of a resort
     */
    @Transactional
    public SkiResort fetchElevationProfilesForResort(Long resortId) {
        SkiResort resort = skiResortRepository.findByIdWithLifts(resortId)
                .orElseThrow(() -> new RuntimeException("Resort not found"));

        for (Lift lift : resort.getLifts()) {
            if (lift.getStartLatitude() != null && lift.getEndLatitude() != null) {
                try {
                    fetchElevationProfileForLift(lift);
                    Thread.sleep(500); // Rate limiting
                } catch (Exception e) {
                    log.warn("Failed to fetch elevation for lift {}: {}", lift.getName(), e.getMessage());
                }
            }
        }

        return skiResortRepository.save(resort);
    }

    private SkiResort importResort(ResortInfo info) {
        // Check if resort already exists
        List<SkiResort> existing = skiResortRepository.findByNameContainingIgnoreCase(info.name());
        if (!existing.isEmpty()) {
            log.info("Resort {} already exists, updating...", info.name());
            return updateResort(existing.get(0), info);
        }

        // Create new resort
        SkiResort resort = SkiResort.builder()
                .name(info.name())
                .region(info.region())
                .canton(info.canton())
                .latitude(info.lat())
                .longitude(info.lon())
                .build();

        // Calculate drive time from Zurich
        Integer driveTime = routingService.calculateDriveTimeFromZurich(info.lat(), info.lon());
        resort.setDriveTimeFromZurichMinutes(driveTime);

        // Save resort first to get ID
        resort = skiResortRepository.save(resort);

        // Fetch lifts from OSM
        List<Map<String, Object>> osmLifts = overpassApiService.fetchLiftsNearPoint(
                info.lat(), info.lon(), info.searchRadius());

        List<Lift> lifts = parseLifts(osmLifts, resort);
        resort.getLifts().addAll(lifts);

        // Fetch pistes to count slopes
        List<Map<String, Object>> osmPistes = overpassApiService.fetchPistesInArea(
                info.lat() - 0.1, info.lon() - 0.1,
                info.lat() + 0.1, info.lon() + 0.1);

        updateSlopeCounts(resort, osmPistes);

        // Calculate elevation range from lifts
        updateElevationRange(resort);

        return skiResortRepository.save(resort);
    }

    private SkiResort updateResort(SkiResort resort, ResortInfo info) {
        resort.setRegion(info.region());
        resort.setCanton(info.canton());
        resort.setLatitude(info.lat());
        resort.setLongitude(info.lon());

        // Update drive time
        Integer driveTime = routingService.calculateDriveTimeFromZurich(info.lat(), info.lon());
        resort.setDriveTimeFromZurichMinutes(driveTime);

        // Clear and re-import lifts
        resort.getLifts().clear();

        List<Map<String, Object>> osmLifts = overpassApiService.fetchLiftsNearPoint(
                info.lat(), info.lon(), info.searchRadius());

        List<Lift> lifts = parseLifts(osmLifts, resort);
        resort.getLifts().addAll(lifts);

        // Update piste counts
        List<Map<String, Object>> osmPistes = overpassApiService.fetchPistesInArea(
                info.lat() - 0.1, info.lon() - 0.1,
                info.lat() + 0.1, info.lon() + 0.1);

        updateSlopeCounts(resort, osmPistes);
        updateElevationRange(resort);

        return skiResortRepository.save(resort);
    }

    @SuppressWarnings("unchecked")
    private List<Lift> parseLifts(List<Map<String, Object>> osmLifts, SkiResort resort) {
        List<Lift> lifts = new ArrayList<>();
        Set<String> processedNames = new HashSet<>();

        log.info("Parsing {} OSM elements for resort {}", osmLifts.size(), resort.getName());

        for (Map<String, Object> osmLift : osmLifts) {
            try {
                Map<String, Object> tags = (Map<String, Object>) osmLift.get("tags");
                if (tags == null) {
                    log.debug("Skipping element without tags: {}", osmLift.get("id"));
                    continue;
                }

                String rawAerialway = tags.get("aerialway") != null ? tags.get("aerialway").toString() : "null";
                String name = (String) tags.getOrDefault("name", "Unnamed Lift #" + osmLift.get("id"));

                // Skip duplicates
                if (processedNames.contains(name)) {
                    log.debug("Skipping duplicate lift: {}", name);
                    continue;
                }
                processedNames.add(name);

                String aerialwayType = overpassApiService.parseAerialwayType(tags);
                if (aerialwayType == null) {
                    log.debug("Skipping non-transport aerialway: {} (type: {})", name, rawAerialway);
                    continue;
                }

                Lift lift = Lift.builder()
                        .name(name)
                        .liftType(LiftType.valueOf(aerialwayType))
                        .skiResort(resort)
                        .build();

                // Extract geometry
                List<Map<String, Object>> geometry = (List<Map<String, Object>>) osmLift.get("geometry");
                if (geometry != null && geometry.size() >= 2) {
                    Map<String, Object> start = geometry.get(0);
                    Map<String, Object> end = geometry.get(geometry.size() - 1);

                    lift.setStartLatitude(((Number) start.get("lat")).doubleValue());
                    lift.setStartLongitude(((Number) start.get("lon")).doubleValue());
                    lift.setEndLatitude(((Number) end.get("lat")).doubleValue());
                    lift.setEndLongitude(((Number) end.get("lon")).doubleValue());

                    // Calculate length
                    double length = openElevationService.calculateDistance(
                            lift.getStartLatitude(), lift.getStartLongitude(),
                            lift.getEndLatitude(), lift.getEndLongitude());
                    lift.setLengthMeters((int) length);
                }

                // Extract OSM ID
                if (osmLift.get("id") != null) {
                    lift.setOsmWayId(((Number) osmLift.get("id")).longValue());
                }

                // Extract capacity if available
                if (tags.get("aerialway:capacity") != null) {
                    try {
                        lift.setCapacityPerHour(Integer.parseInt(tags.get("aerialway:capacity").toString()));
                    } catch (NumberFormatException ignored) {}
                }

                lifts.add(lift);

            } catch (Exception e) {
                log.warn("Failed to parse lift: {}", e.getMessage());
            }
        }

        log.info("Parsed {} lifts for resort {}", lifts.size(), resort.getName());
        return lifts;
    }

    @SuppressWarnings("unchecked")
    private void updateSlopeCounts(SkiResort resort, List<Map<String, Object>> osmPistes) {
        AtomicInteger blue = new AtomicInteger(0);
        AtomicInteger red = new AtomicInteger(0);
        AtomicInteger black = new AtomicInteger(0);
        double totalLength = 0;

        for (Map<String, Object> piste : osmPistes) {
            Map<String, Object> tags = (Map<String, Object>) piste.get("tags");
            if (tags == null) continue;

            String difficulty = overpassApiService.parsePisteDifficulty(tags);
            if (difficulty == null) continue;

            switch (difficulty) {
                case "blue" -> blue.incrementAndGet();
                case "red" -> red.incrementAndGet();
                case "black" -> black.incrementAndGet();
            }

            // Calculate piste length if geometry available
            List<Map<String, Object>> geometry = (List<Map<String, Object>>) piste.get("geometry");
            if (geometry != null && geometry.size() >= 2) {
                for (int i = 1; i < geometry.size(); i++) {
                    Map<String, Object> p1 = geometry.get(i - 1);
                    Map<String, Object> p2 = geometry.get(i);
                    totalLength += openElevationService.calculateDistance(
                            ((Number) p1.get("lat")).doubleValue(),
                            ((Number) p1.get("lon")).doubleValue(),
                            ((Number) p2.get("lat")).doubleValue(),
                            ((Number) p2.get("lon")).doubleValue()
                    );
                }
            }
        }

        resort.setBlueSlopes(blue.get());
        resort.setRedSlopes(red.get());
        resort.setBlackSlopes(black.get());
        resort.setTotalSlopeKm(Math.round(totalLength / 100) / 10.0); // Round to 1 decimal
    }

    private void updateElevationRange(SkiResort resort) {
        if (resort.getLifts().isEmpty()) return;

        int minElev = Integer.MAX_VALUE;
        int maxElev = Integer.MIN_VALUE;

        for (Lift lift : resort.getLifts()) {
            if (lift.getStartElevation() != null) {
                minElev = Math.min(minElev, lift.getStartElevation());
                maxElev = Math.max(maxElev, lift.getStartElevation());
            }
            if (lift.getEndElevation() != null) {
                minElev = Math.min(minElev, lift.getEndElevation());
                maxElev = Math.max(maxElev, lift.getEndElevation());
            }
        }

        if (minElev != Integer.MAX_VALUE) {
            resort.setMinElevation(minElev);
        }
        if (maxElev != Integer.MIN_VALUE) {
            resort.setMaxElevation(maxElev);
        }
    }

    private void fetchElevationProfileForLift(Lift lift) {
        if (lift.getStartLatitude() == null || lift.getEndLatitude() == null) return;

        lift.getElevationProfile().clear();

        List<double[]> coordinates = openElevationService.interpolatePoints(
                lift.getStartLatitude(), lift.getStartLongitude(),
                lift.getEndLatitude(), lift.getEndLongitude(),
                10);

        List<Double> elevations = openElevationService.getElevations(coordinates);

        double cumulativeDistance = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            double[] coord = coordinates.get(i);

            if (i > 0) {
                double[] prevCoord = coordinates.get(i - 1);
                cumulativeDistance += openElevationService.calculateDistance(
                        prevCoord[0], prevCoord[1], coord[0], coord[1]);
            }

            ElevationPoint point = ElevationPoint.builder()
                    .sequenceNumber(i)
                    .latitude(coord[0])
                    .longitude(coord[1])
                    .elevation(i < elevations.size() ? elevations.get(i) : null)
                    .distanceFromStart(cumulativeDistance)
                    .lift(lift)
                    .build();

            lift.getElevationProfile().add(point);
        }

        if (!elevations.isEmpty()) {
            lift.setStartElevation(elevations.get(0).intValue());
            lift.setEndElevation(elevations.get(elevations.size() - 1).intValue());
        }
    }
}
