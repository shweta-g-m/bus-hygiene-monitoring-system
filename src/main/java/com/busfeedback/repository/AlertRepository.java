package com.busfeedback.repository;
import com.busfeedback.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    // Pending only — for Active Alerts view
    List<Alert> findByStatusOrderByCreatedAtDesc(String status);
    List<Alert> findTop10ByStatusOrderByCreatedAtDesc(String status);
    // All — for history
    List<Alert> findAllByOrderByCreatedAtDesc();
    List<Alert> findTop10ByOrderByCreatedAtDesc();
    // Safe lookups using unique FK — no duplicate risk
    Optional<Alert> findByFeedbackId(Long feedbackId);
    long countByStatus(String status);
}
