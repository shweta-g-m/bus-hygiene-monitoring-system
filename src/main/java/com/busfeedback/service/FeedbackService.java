package com.busfeedback.service;

import com.busfeedback.model.*;
import com.busfeedback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired private FeedbackRepository      feedbackRepo;
    @Autowired private AlertRepository         alertRepo;
    @Autowired private HygieneStatusRepository hygieneRepo;

    // ── Unique Complaint ID: CMP-YYYYMMDD-NNNN ───────────────
    private synchronized String generateComplaintId() {
        String date  = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long   today = feedbackRepo.countTodayFeedback();
        return String.format("CMP-%s-%04d", date, today + 1);
    }

    // ── Submit: ONE feedback = ONE complaint = ONE alert ──────
    public void saveFeedback(Feedback feedback) {
        feedback.setComplaintId(generateComplaintId());
        feedbackRepo.save(feedback);

        String busId     = feedback.getBusId();
        String cmpId     = feedback.getComplaintId();
        String alertType;
        String message;

        // Determine single alert type (priority: VOMIT > LOW_RATING > HYGIENE > GENERAL)
        if (feedback.isVomit()) {
            alertType = "VOMIT";
            message   = "IMMEDIATE: Vomit reported on Bus " + busId + " [" + cmpId + "]. Urgent cleaning required!";
            markUnclean(busId);
        } else if (feedback.getRating() > 0 && feedback.getRating() <= 2) {
            alertType = "LOW_RATING";
            message   = "Bus " + busId + " rated " + feedback.getRating() + "/5 [" + cmpId + "].";
            markUnclean(busId);
        } else if (feedback.isBadSmell() || feedback.isMuddyFloor() || feedback.isDirtySeats()) {
            alertType = "HYGIENE_COMPLAINT";
            message   = "Hygiene issues on Bus " + busId + " [" + cmpId + "]: " + buildIssueList(feedback);
            incrementComplaint(busId);
        } else {
            alertType = "GENERAL_FEEDBACK";
            message   = "General feedback for Bus " + busId + " [" + cmpId + "].";
        }

        // ONE alert only per feedback submission
        Alert alert = new Alert();
        alert.setComplaintId(cmpId);
        alert.setBusId(busId);
        alert.setAlertType(alertType);
        alert.setMessage(message);
        alert.setStatus("Pending");
        alert.setFeedbackId(feedback.getId());
        alertRepo.save(alert);
    }

    private String buildIssueList(Feedback f) {
        StringBuilder sb = new StringBuilder();
        if (f.isBadSmell())   sb.append("Bad Smell, ");
        if (f.isMuddyFloor()) sb.append("Muddy Floor, ");
        if (f.isDirtySeats()) sb.append("Dirty Seats, ");
        if (sb.length() > 2)  sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    // ── Mark alert Solved → removes from active alerts page ──
    public void markAlertSolved(Long alertId) {
        alertRepo.findById(alertId).ifPresent(a -> {
            a.setStatus("Solved");
            alertRepo.save(a);
            // Sync linked feedback to Resolved using feedbackId (always unique)
            if (a.getFeedbackId() != null) {
                feedbackRepo.findById(a.getFeedbackId()).ifPresent(f -> {
                    f.setStatus("Resolved");
                    feedbackRepo.save(f);
                });
            }
        });
    }

    // ── Toggle feedback: also syncs linked alert ──────────────
    public void toggleFeedbackStatus(Long id) {
        feedbackRepo.findById(id).ifPresent(f -> {
            boolean resolving = !"Resolved".equals(f.getStatus());
            f.setStatus(resolving ? "Resolved" : "Pending");
            feedbackRepo.save(f);
            alertRepo.findByFeedbackId(id).ifPresent(a -> {
                a.setStatus(resolving ? "Solved" : "Pending");
                alertRepo.save(a);
            });
        });
    }

    // ── Delete: removes alert via feedbackId FK first ─────────
    public void deleteFeedback(Long id) {
        alertRepo.findByFeedbackId(id).ifPresent(alertRepo::delete);
        feedbackRepo.deleteById(id);
    }

    // ── Hygiene helpers ───────────────────────────────────────
    private void markUnclean(String busId) {
        HygieneStatus hs = hygieneRepo.findByBusId(busId).orElseGet(() -> newHygiene(busId));
        hs.setClean(false);
        hygieneRepo.save(hs);
    }

    private void incrementComplaint(String busId) {
        HygieneStatus hs = hygieneRepo.findByBusId(busId).orElseGet(() -> newHygiene(busId));
        hs.setComplaintCount(hs.getComplaintCount() + 1);
        if (hs.getComplaintCount() >= 2) hs.setClean(false);
        hygieneRepo.save(hs);
    }

    private HygieneStatus newHygiene(String busId) {
        HygieneStatus h = new HygieneStatus();
        h.setBusId(busId); h.setClean(true); h.setComplaintCount(0);
        return h;
    }

    // ── Dashboard queries ─────────────────────────────────────
    public List<Feedback> getAll()    { return feedbackRepo.findAllByOrderBySubmittedAtDesc(); }
    public List<Feedback> getTop10()  { return feedbackRepo.findTop10ByOrderBySubmittedAtDesc(); }

    public List<Feedback> getFiltered(String busId, String status) {
        if (busId != null && !busId.isEmpty() && status != null && !status.isEmpty())
            return feedbackRepo.findByBusIdAndStatusOrderBySubmittedAtDesc(busId, status);
        if (busId != null && !busId.isEmpty())
            return feedbackRepo.findByBusIdOrderBySubmittedAtDesc(busId);
        if (status != null && !status.isEmpty())
            return feedbackRepo.findByStatusOrderBySubmittedAtDesc(status);
        return feedbackRepo.findAllByOrderBySubmittedAtDesc();
    }

    public long   countTotal()    { return feedbackRepo.count(); }
    public long   countPending()  { return feedbackRepo.countByStatus("Pending"); }
    public long   countResolved() { return feedbackRepo.countByStatus("Resolved"); }
    public String avgRating()     { return String.format("%.1f", feedbackRepo.findAverageRating()); }

    // ── Alert queries (active = Pending only) ─────────────────
    public List<Alert> getActiveAlerts()  { return alertRepo.findByStatusOrderByCreatedAtDesc("Pending"); }
    public List<Alert> getTop10Active()   { return alertRepo.findTop10ByStatusOrderByCreatedAtDesc("Pending"); }
    public List<Alert> getAllAlerts()      { return alertRepo.findAllByOrderByCreatedAtDesc(); }

    public long countTotalAlerts()   { return alertRepo.count(); }
    public long countPendingAlerts() { return alertRepo.countByStatus("Pending"); }
    public long countSolvedAlerts()  { return alertRepo.countByStatus("Solved"); }

    // ── Hygiene status ────────────────────────────────────────
    public long countCleanBuses()   { return hygieneRepo.countByIsClean(true); }
    public long countUncleanBuses() { return hygieneRepo.countByIsClean(false); }

    public void markBusClean(String busId) {
        HygieneStatus hs = hygieneRepo.findByBusId(busId).orElseGet(() -> newHygiene(busId));
        hs.setClean(true); hs.setComplaintCount(0);
        hygieneRepo.save(hs);
    }
}
