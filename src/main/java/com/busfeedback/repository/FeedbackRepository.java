package com.busfeedback.repository;
import com.busfeedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByOrderBySubmittedAtDesc();
    List<Feedback> findTop10ByOrderBySubmittedAtDesc();
    List<Feedback> findByBusIdOrderBySubmittedAtDesc(String busId);
    List<Feedback> findByStatusOrderBySubmittedAtDesc(String status);
    List<Feedback> findByBusIdAndStatusOrderBySubmittedAtDesc(String busId, String status);
    long countByStatus(String status);
    long countByBusIdAndStatus(String busId, String status);
    Optional<Feedback> findByComplaintId(String complaintId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE DATE(f.submittedAt) = CURRENT_DATE")
    long countTodayFeedback();

    @Query("SELECT COALESCE(AVG(f.rating), 0) FROM Feedback f")
    double findAverageRating();
}
