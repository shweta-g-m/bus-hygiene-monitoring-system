package com.busfeedback.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "sbhms_feedback")
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_id", nullable = false, unique = true, length = 25)
    private String complaintId;

    @Column(name = "bus_id", nullable = false, length = 20)   private String busId;
    @Column(name = "route",  nullable = false, length = 150)  private String route;
    @Column(name = "rating")                                   private int rating;
    @Column(name = "bad_smell")                                private boolean badSmell;
    @Column(name = "muddy_floor")                              private boolean muddyFloor;
    @Column(name = "dirty_seats")                              private boolean dirtySeats;
    @Column(name = "vomit")                                    private boolean vomit;
    @Column(name = "cleaning_areas", length = 200)             private String cleaningAreas;
    @Column(name = "complaint", columnDefinition = "TEXT")     private String complaint;
    @Column(name = "status", length = 20)                      private String status = "Pending";
    @Column(name = "submitted_at")                             private LocalDateTime submittedAt;

    @PrePersist
    public void prePersist() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
        if (status == null)      status      = "Pending";
    }
}
