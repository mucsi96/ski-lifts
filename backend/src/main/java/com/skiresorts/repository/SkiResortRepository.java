package com.skiresorts.repository;

import com.skiresorts.model.SkiResort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkiResortRepository extends JpaRepository<SkiResort, Long> {

    List<SkiResort> findByRegionIgnoreCase(String region);

    List<SkiResort> findByCantonIgnoreCase(String canton);

    @Query("SELECT sr FROM SkiResort sr WHERE sr.driveTimeFromZurichMinutes <= :maxMinutes")
    List<SkiResort> findByMaxDriveTimeFromZurich(Integer maxMinutes);

    @Query("SELECT sr FROM SkiResort sr LEFT JOIN FETCH sr.lifts WHERE sr.id = :id")
    Optional<SkiResort> findByIdWithLifts(Long id);

    List<SkiResort> findByNameContainingIgnoreCase(String name);
}
