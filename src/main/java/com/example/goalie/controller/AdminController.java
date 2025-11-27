package com.example.goalie.controller;

import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import com.example.goalie.model.User;
import com.example.goalie.repository.TeamRepository;
import com.example.goalie.repository.TournamentRepository;
import com.example.goalie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;

    @GetMapping("/admin")
    public String adminDashboard(Model model, Principal principal) {
        // Optional: fetch the current admin
        String adminEmail = principal.getName();
        model.addAttribute("adminEmail", adminEmail);

        // Fetch data for the dashboard
        List<User> users = userRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        List<Tournament> tournaments = tournamentRepository.findAll();

        // Summary stats
        long totalUsers = users.size();
        tournaments.stream()
                .filter(t -> t.getStatus().equals("Active")) // exact match, case-sensitive
                .count();// exact match, case-sensitive
        long activeTournaments = 0;


        // Add all to model
        model.addAttribute("users", users);
        model.addAttribute("teams", teams);
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeTournaments", activeTournaments);
        model.addAttribute("pendingReports", 0); // example placeholder
        model.addAttribute("systemStatus", "Online"); // example placeholder

        return "admin";
    }
}
