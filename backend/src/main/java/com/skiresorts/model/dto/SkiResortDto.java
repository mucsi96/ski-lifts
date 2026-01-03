package com.skiresorts.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkiResortDto {
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
    private Integer liftCount;
}
