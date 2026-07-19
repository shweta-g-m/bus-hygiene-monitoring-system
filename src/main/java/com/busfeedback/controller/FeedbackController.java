package com.busfeedback.controller;

import com.busfeedback.model.Feedback;
import com.busfeedback.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired private FeedbackService service;

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody Map<String, String> body) {
        String busId     = body.getOrDefault("busId",         "").trim();
        String route     = body.getOrDefault("route",         "").trim();
        String ratingS   = body.getOrDefault("rating",        "0");
        String cleaning  = body.getOrDefault("cleaningAreas", "").trim();
        String complaint = body.getOrDefault("complaint",     "").trim();

        // Block unknown or empty busId
        if (busId.isEmpty() || "UNKNOWN".equalsIgnoreCase(busId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid Bus ID. Please scan the QR code inside your bus."));
        }
        if (route.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Route is required."));
        }

        boolean badSmell   = "true".equalsIgnoreCase(body.getOrDefault("badSmell",   "false"));
        boolean muddyFloor = "true".equalsIgnoreCase(body.getOrDefault("muddyFloor", "false"));
        boolean dirtySeats = "true".equalsIgnoreCase(body.getOrDefault("dirtySeats", "false"));
        boolean vomit      = "true".equalsIgnoreCase(body.getOrDefault("vomit",      "false"));

        Feedback f = new Feedback();
        f.setBusId(busId);
        f.setRoute(route);
        f.setRating(Integer.parseInt(ratingS.isEmpty() ? "0" : ratingS));
        f.setBadSmell(badSmell);
        f.setMuddyFloor(muddyFloor);
        f.setDirtySeats(dirtySeats);
        f.setVomit(vomit);
        f.setCleaningAreas(cleaning);
        f.setComplaint(complaint);

        service.saveFeedback(f);
        return ResponseEntity.ok(Map.of(
                "message",     "Feedback submitted successfully!",
                "complaintId", f.getComplaintId()
        ));
    }
}
