package com.busfeedback.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "sbhms_hygiene_status")
public class HygieneStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bus_id",          nullable = false, unique = true, length = 20) private String busId;
    @Column(name = "is_clean")                                                       private boolean isClean = true;
    @Column(name = "complaint_count")                                                private int complaintCount = 0;
    @Column(name = "last_updated")                                                   private LocalDateTime lastUpdated;

    @PrePersist @PreUpdate
    public void updateTimestamp() { lastUpdated = LocalDateTime.now(); }
}
