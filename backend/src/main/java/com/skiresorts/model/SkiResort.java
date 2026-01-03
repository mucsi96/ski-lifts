package com.skiresorts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ski_resorts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkiResort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String region;

    private String canton;

    @Column(name = "drive_time_from_zurich_minutes")
    private Integer driveTimeFromZurichMinutes;

    @Column(name = "min_elevation")
    private Integer minElevation;

    @Column(name = "max_elevation")
    private Integer maxElevation;

    @Column(name = "blue_slopes")
    private Integer blueSlopes;

    @Column(name = "red_slopes")
    private Integer redSlopes;

    @Column(name = "black_slopes")
    private Integer blackSlopes;

    @Column(name = "total_slope_km")
    private Double totalSlopeKm;

    private Double latitude;
    private Double longitude;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "osm_relation_id")
    private Long osmRelationId;

    @OneToMany(mappedBy = "skiResort", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Lift> lifts = new ArrayList<>();
}
