package com.skiresorts.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElevationPointDto {
    private Double latitude;
    private Double longitude;
    private Double elevation;
    private Double distanceFromStart;
}
