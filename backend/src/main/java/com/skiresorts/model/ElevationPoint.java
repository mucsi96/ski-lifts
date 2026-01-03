package com.skiresorts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "elevation_points")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElevationPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    private Double latitude;
    private Double longitude;
    private Double elevation;

    @Column(name = "distance_from_start")
    private Double distanceFromStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lift_id")
    private Lift lift;
}
