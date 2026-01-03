package com.skiresorts.repository;

import com.skiresorts.model.Lift;
import com.skiresorts.model.LiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiftRepository extends JpaRepository<Lift, Long> {

    List<Lift> findBySkiResortId(Long skiResortId);

    List<Lift> findByLiftType(LiftType liftType);

    @Query("SELECT l FROM Lift l LEFT JOIN FETCH l.elevationProfile WHERE l.id = :id")
    Optional<Lift> findByIdWithElevationProfile(Long id);

    @Query("SELECT l FROM Lift l LEFT JOIN FETCH l.elevationProfile WHERE l.skiResort.id = :resortId")
    List<Lift> findBySkiResortIdWithElevationProfile(Long resortId);
}
