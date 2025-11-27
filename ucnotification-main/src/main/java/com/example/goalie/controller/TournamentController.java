package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Tournament;
import com.example.goalie.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final AppService service;

    // 1️⃣ List all tournaments
    @GetMapping
    public String listTournaments(@RequestParam(required = false) String search,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false) String location,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  Model model) {

        List<Tournament> tournaments;

        boolean hasFilters = (search != null && !search.trim().isEmpty()) ||
                (status != null && !status.trim().isEmpty()) ||
                (location != null && !location.trim().isEmpty()) ||
                startDate != null ||
                endDate != null;

        if (hasFilters) {
            tournaments = service.searchAndFilterTournaments(search, status, location, startDate, endDate);
        } else {
            tournaments = service.getAllTournaments();
        }

        // ✅ Update status for each tournament
        tournaments.forEach(service::updateTournamentStatus);

        model.addAttribute("tournaments", tournaments);
        model.addAttribute("locations", service.getAllLocations());
        model.addAttribute("statuses", Tournament.TournamentStatus.values());
        model.addAttribute("currentSearch", search != null ? search : "");
        model.addAttribute("currentStatus", status != null ? status : "");
        model.addAttribute("currentLocation", location != null ? location : "");
        model.addAttribute("currentStartDate", startDate != null ? startDate.toString() : "");
        model.addAttribute("currentEndDate", endDate != null ? endDate.toString() : "");

        return "tournaments"; // templates/tournaments.html
    }

    // 2️⃣ Show create tournament form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("tournament", new Tournament());
        return "create_tournament"; // templates/create_tournament.html
    }

    // 3️⃣ Handle tournament creation
    @PostMapping("/create")
    public String createTournament(@ModelAttribute Tournament tournament,
                                   Principal principal) {
        if (principal == null) return "redirect:/login";
        
        String email = principal.getName();
        User organizer = service.getUserByEmail(email);
        
        if (organizer == null) return "redirect:/login";

        tournament.setStatus(Tournament.TournamentStatus.UPCOMING);
        Tournament createdTournament = service.createTournament(tournament, organizer);
        
        // Create a notification for the organizer
        service.createNotification(organizer, "You successfully created tournament '" + createdTournament.getName() + "'");

        return "redirect:/tournaments"; // back to tournament list
    }

    // 4️⃣ View tournament details
    @GetMapping("/{id}")
    public String viewTournament(@PathVariable Long id, Model model) {
        Tournament tournament = service.getTournamentById(id);
        if (tournament != null) {
            // ✅ Update status based on current date
            service.updateTournamentStatus(tournament);
        }
        model.addAttribute("tournament", tournament);
        return "view_tournament"; // templates/view_tournament.html
    }

    // 5️⃣ Delete tournament
    @PostMapping("/{id}/delete")
    public String deleteTournament(@PathVariable Long id) {
        Tournament tournament = service.getTournamentById(id);
        if (tournament != null) {
            service.deleteTournament(tournament);
        }
        return "redirect:/tournaments";
    }
}
