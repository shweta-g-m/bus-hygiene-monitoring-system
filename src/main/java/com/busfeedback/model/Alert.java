package com.busfeedback.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "sbhms_alerts")
public class Alert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_id", nullable = false, length = 25) private String complaintId;
    @Column(name = "bus_id",       nullable = false, length = 20) private String busId;
    @Column(name = "alert_type",   nullable = false, length = 50) private String alertType;
    @Column(name = "message",      columnDefinition = "TEXT")     private String message;
    @Column(name = "status",       length = 20)                   private String status = "Pending";
    @Column(name = "created_at")                                  private LocalDateTime createdAt;
    @Column(name = "feedback_id",  unique = true)                 private Long feedbackId;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status    == null) status    = "Pending";
    }
}
