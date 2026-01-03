package com.skiresorts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lifts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "lift_type")
    private LiftType liftType;

    @Column(name = "start_elevation")
    private Integer startElevation;

    @Column(name = "end_elevation")
    private Integer endElevation;

    @Column(name = "length_meters")
    private Integer lengthMeters;

    @Column(name = "capacity_per_hour")
    private Integer capacityPerHour;

    @Column(name = "start_latitude")
    private Double startLatitude;

    @Column(name = "start_longitude")
    private Double startLongitude;

    @Column(name = "end_latitude")
    private Double endLatitude;

    @Column(name = "end_longitude")
    private Double endLongitude;

    @Column(name = "osm_way_id")
    private Long osmWayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ski_resort_id")
    private SkiResort skiResort;

    @OneToMany(mappedBy = "lift", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("sequenceNumber ASC")
    private List<ElevationPoint> elevationProfile = new ArrayList<>();
}
