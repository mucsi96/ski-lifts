package com.skiresorts.controller;

import com.skiresorts.model.dto.LiftDto;
import com.skiresorts.model.dto.SkiResortDetailDto;
import com.skiresorts.model.dto.SkiResortDto;
import com.skiresorts.service.SkiResortService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resorts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SkiResortController {

    private final SkiResortService skiResortService;

    @GetMapping
    public ResponseEntity<List<SkiResortDto>> getAllResorts() {
        return ResponseEntity.ok(skiResortService.getAllResorts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkiResortDetailDto> getResortById(@PathVariable Long id) {
        return skiResortService.getResortById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<SkiResortDto>> getResortsByRegion(@PathVariable String region) {
        return ResponseEntity.ok(skiResortService.getResortsByRegion(region));
    }

    @GetMapping("/drive-time")
    public ResponseEntity<List<SkiResortDto>> getResortsByDriveTime(
            @RequestParam(defaultValue = "120") Integer maxMinutes) {
        return ResponseEntity.ok(skiResortService.getResortsByMaxDriveTime(maxMinutes));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SkiResortDto>> searchResorts(@RequestParam String q) {
        return ResponseEntity.ok(skiResortService.searchResorts(q));
    }

    @GetMapping("/{resortId}/lifts")
    public ResponseEntity<List<LiftDto>> getResortLifts(@PathVariable Long resortId) {
        return ResponseEntity.ok(skiResortService.getLiftsByResort(resortId));
    }

    @GetMapping("/lifts/{liftId}")
    public ResponseEntity<LiftDto> getLiftById(@PathVariable Long liftId) {
        return skiResortService.getLiftById(liftId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/lifts/{liftId}/elevation-profile")
    public ResponseEntity<LiftDto> fetchElevationProfile(
            @PathVariable Long liftId,
            @RequestParam(defaultValue = "20") Integer numPoints) {
        try {
            return ResponseEntity.ok(skiResortService.fetchAndSaveElevationProfile(liftId, numPoints));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
