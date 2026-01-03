package com.skiresorts.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SkiResortDetailDto {
    private Long id;
    private String name;
    private String region;
    private String canton;
    private Integer driveTimeFromZurichMinutes;
    private Integer minElevation;
    private Integer maxElevation;
    private Integer blueSlopes;
    private Integer redSlopes;
    private Integer blackSlopes;
    private Double totalSlopeKm;
    private Double latitude;
    private Double longitude;
    private String websiteUrl;
    private List<LiftDto> lifts;
}
