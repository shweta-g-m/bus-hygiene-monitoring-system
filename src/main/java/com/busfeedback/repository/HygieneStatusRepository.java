package com.busfeedback.repository;

import com.busfeedback.model.HygieneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HygieneStatusRepository extends JpaRepository<HygieneStatus, Long> {
    Optional<HygieneStatus> findByBusId(String busId);
    long countByIsClean(boolean isClean);
}
