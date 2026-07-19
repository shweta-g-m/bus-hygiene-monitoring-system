package com.busfeedback.controller;

import com.busfeedback.model.Admin;
import com.busfeedback.repository.AdminRepository;
import com.busfeedback.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FeedbackService service;

    @Autowired
    private AdminRepository adminRepo;

    // ── Login Page ─────────────────────────────────────────────
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("adminLoggedIn") != null) {
            return "redirect:/admin/dashboard";
        }
        return "admin-login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        Optional<Admin> admin = adminRepo.findByEmailAndPassword(email.trim(), password.trim());
        if (admin.isPresent()) {
            session.setAttribute("adminLoggedIn", true);
            session.setAttribute("adminName", admin.get().getName());
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Invalid email or password. Please try again.");
        return "admin-login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // ── Dashboard ──────────────────────────────────────────────
    @GetMapping
    public String dashboardRedirect() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String filterBus,
                            @RequestParam(required = false) String filterStatus,
                            HttpSession session,
                            Model model) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("adminName",     session.getAttribute("adminName"));
        model.addAttribute("feedbackList",  service.getFiltered(filterBus, filterStatus));
        model.addAttribute("recentAlerts",  service.getTop10Active());
        model.addAttribute("totalFeedback", service.countTotal());
        model.addAttribute("totalAlerts",   service.countTotalAlerts());
        model.addAttribute("pendingAlerts", service.countPendingAlerts());
        model.addAttribute("solvedAlerts",  service.countSolvedAlerts());
        model.addAttribute("cleanBuses",    service.countCleanBuses());
        model.addAttribute("uncleanBuses",  service.countUncleanBuses());
        model.addAttribute("pending",       service.countPending());
        model.addAttribute("resolved",      service.countResolved());
        model.addAttribute("avgRating",     service.avgRating());
        model.addAttribute("filterBus",     filterBus    != null ? filterBus    : "");
        model.addAttribute("filterStatus",  filterStatus != null ? filterStatus : "");
        return "admin-dashboard";
    }

    // ── Toggle Feedback Status ─────────────────────────────────
    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id,
                               @RequestParam(required = false) String filterBus,
                               @RequestParam(required = false) String filterStatus,
                               HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin/login";
        service.toggleFeedbackStatus(id);
        return buildRedirect(filterBus, filterStatus);
    }

    // ── Delete Feedback ────────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id,
                                 @RequestParam(required = false) String filterBus,
                                 @RequestParam(required = false) String filterStatus,
                                 HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin/login";
        service.deleteFeedback(id);
        return buildRedirect(filterBus, filterStatus);
    }

    // ── Mark Alert as Solved ───────────────────────────────────
    @PostMapping("/alert/solve/{id}")
    public String solveAlert(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin/login";
        service.markAlertSolved(id);
        return "redirect:/admin/dashboard";
    }

    // ── Mark Bus as Clean ──────────────────────────────────────
    @PostMapping("/bus/clean/{busId}")
    public String markBusClean(@PathVariable String busId, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin/login";
        service.markBusClean(busId);
        return "redirect:/admin/dashboard";
    }

    private String buildRedirect(String filterBus, String filterStatus) {
        String redirect = "redirect:/admin/dashboard";
        boolean hasParam = false;
        if (filterBus != null && !filterBus.isEmpty()) {
            redirect += "?filterBus=" + filterBus;
            hasParam = true;
        }
        if (filterStatus != null && !filterStatus.isEmpty())
            redirect += (hasParam ? "&" : "?") + "filterStatus=" + filterStatus;
        return redirect;
    }
}
