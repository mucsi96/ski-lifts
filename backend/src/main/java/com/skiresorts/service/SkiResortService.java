package com.skiresorts.service;

import com.skiresorts.model.ElevationPoint;
import com.skiresorts.model.Lift;
import com.skiresorts.model.SkiResort;
import com.skiresorts.model.dto.*;
import com.skiresorts.repository.LiftRepository;
import com.skiresorts.repository.SkiResortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkiResortService {

    private final SkiResortRepository skiResortRepository;
    private final LiftRepository liftRepository;
    private final OpenElevationService openElevationService;

    @Transactional(readOnly = true)
    public List<SkiResortDto> getAllResorts() {
        return skiResortRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<SkiResortDetailDto> getResortById(Long id) {
        return skiResortRepository.findByIdWithLifts(id)
                .map(this::toDetailDto);
    }

    @Transactional(readOnly = true)
    public List<SkiResortDto> getResortsByRegion(String region) {
        return skiResortRepository.findByRegionIgnoreCase(region).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SkiResortDto> getResortsByMaxDriveTime(Integer maxMinutes) {
        return skiResortRepository.findByMaxDriveTimeFromZurich(maxMinutes).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SkiResortDto> searchResorts(String query) {
        return skiResortRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LiftDto> getLiftsByResort(Long resortId) {
        return liftRepository.findBySkiResortIdWithElevationProfile(resortId).stream()
                .map(this::toLiftDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<LiftDto> getLiftById(Long liftId) {
        return liftRepository.findByIdWithElevationProfile(liftId)
                .map(this::toLiftDto);
    }

    @Transactional
    public LiftDto fetchAndSaveElevationProfile(Long liftId, int numPoints) {
        Lift lift = liftRepository.findById(liftId)
                .orElseThrow(() -> new RuntimeException("Lift not found"));

        if (lift.getStartLatitude() == null || lift.getEndLatitude() == null) {
            throw new RuntimeException("Lift coordinates not available");
        }

        // Clear existing elevation points
        lift.getElevationProfile().clear();

        // Interpolate points along the lift
        List<double[]> coordinates = openElevationService.interpolatePoints(
                lift.getStartLatitude(), lift.getStartLongitude(),
                lift.getEndLatitude(), lift.getEndLongitude(),
                numPoints
        );

        // Fetch elevations
        List<Double> elevations = openElevationService.getElevations(coordinates);

        // Calculate distances and create elevation points
        double cumulativeDistance = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            double[] coord = coordinates.get(i);

            if (i > 0) {
                double[] prevCoord = coordinates.get(i - 1);
                cumulativeDistance += openElevationService.calculateDistance(
                        prevCoord[0], prevCoord[1], coord[0], coord[1]
                );
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

        // Update lift elevations from profile
        if (!elevations.isEmpty()) {
            lift.setStartElevation(elevations.get(0).intValue());
            lift.setEndElevation(elevations.get(elevations.size() - 1).intValue());
            lift.setLengthMeters((int) cumulativeDistance);
        }

        liftRepository.save(lift);
        return toLiftDto(lift);
    }

    private SkiResortDto toDto(SkiResort resort) {
        return SkiResortDto.builder()
                .id(resort.getId())
                .name(resort.getName())
                .region(resort.getRegion())
                .canton(resort.getCanton())
                .driveTimeFromZurichMinutes(resort.getDriveTimeFromZurichMinutes())
                .minElevation(resort.getMinElevation())
                .maxElevation(resort.getMaxElevation())
                .blueSlopes(resort.getBlueSlopes())
                .redSlopes(resort.getRedSlopes())
                .blackSlopes(resort.getBlackSlopes())
                .totalSlopeKm(resort.getTotalSlopeKm())
                .latitude(resort.getLatitude())
                .longitude(resort.getLongitude())
                .websiteUrl(resort.getWebsiteUrl())
                .liftCount(resort.getLifts() != null ? resort.getLifts().size() : 0)
                .build();
    }

    private SkiResortDetailDto toDetailDto(SkiResort resort) {
        return SkiResortDetailDto.builder()
                .id(resort.getId())
                .name(resort.getName())
                .region(resort.getRegion())
                .canton(resort.getCanton())
                .driveTimeFromZurichMinutes(resort.getDriveTimeFromZurichMinutes())
                .minElevation(resort.getMinElevation())
                .maxElevation(resort.getMaxElevation())
                .blueSlopes(resort.getBlueSlopes())
                .redSlopes(resort.getRedSlopes())
                .blackSlopes(resort.getBlackSlopes())
                .totalSlopeKm(resort.getTotalSlopeKm())
                .latitude(resort.getLatitude())
                .longitude(resort.getLongitude())
                .websiteUrl(resort.getWebsiteUrl())
                .lifts(resort.getLifts().stream().map(this::toLiftDto).toList())
                .build();
    }

    private LiftDto toLiftDto(Lift lift) {
        return LiftDto.builder()
                .id(lift.getId())
                .name(lift.getName())
                .liftType(lift.getLiftType())
                .liftTypeDisplayName(lift.getLiftType() != null ? lift.getLiftType().getDisplayName() : null)
                .startElevation(lift.getStartElevation())
                .endElevation(lift.getEndElevation())
                .elevationGain(lift.getEndElevation() != null && lift.getStartElevation() != null
                        ? lift.getEndElevation() - lift.getStartElevation() : null)
                .lengthMeters(lift.getLengthMeters())
                .capacityPerHour(lift.getCapacityPerHour())
                .startLatitude(lift.getStartLatitude())
                .startLongitude(lift.getStartLongitude())
                .endLatitude(lift.getEndLatitude())
                .endLongitude(lift.getEndLongitude())
                .elevationProfile(lift.getElevationProfile().stream()
                        .map(this::toElevationPointDto)
                        .toList())
                .build();
    }

    private ElevationPointDto toElevationPointDto(ElevationPoint point) {
        return ElevationPointDto.builder()
                .latitude(point.getLatitude())
                .longitude(point.getLongitude())
                .elevation(point.getElevation())
                .distanceFromStart(point.getDistanceFromStart())
                .build();
    }
}
