package com.skiresorts.model.dto;

import com.skiresorts.model.LiftType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LiftDto {
    private Long id;
    private String name;
    private LiftType liftType;
    private String liftTypeDisplayName;
    private Integer startElevation;
    private Integer endElevation;
    private Integer elevationGain;
    private Integer lengthMeters;
    private Integer capacityPerHour;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
    private List<ElevationPointDto> elevationProfile;
}
