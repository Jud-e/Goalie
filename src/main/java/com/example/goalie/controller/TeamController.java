package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final AppService service;

    // List all teams
    @GetMapping
    public String viewTeams(@RequestParam Long tournamentId, Model model){
        System.out.println("Received Tournament ID: " + tournamentId); // <-- Check this number

        Tournament tournament = service.getTournamentById(tournamentId);

        // 2. Check the result before it crashes
        System.out.println("Tournament Object Retrieved: " + tournament);

        if (tournament == null) {
            // Redirect to a safe page (e.g., the tournament list)
            // You could also add a message here using RedirectAttributes
            return "redirect:/tournaments";
        }
        List<Team> teams = service.getTeamsByTournament(tournament);
        model.addAttribute("tournament", tournament);
        model.addAttribute("teams", teams);
        return "viewTeams";
    }

    // Show create team form
    @GetMapping("/create")
    public String showCreateTeamForm(@RequestParam Long tournamentId, Model model){
        Tournament tournament = service.getTournamentById(tournamentId);
        model.addAttribute("team", new Team());
        model.addAttribute("tournament", tournament);
        return "createTeam";
    }

    // Handle team creation
    @PostMapping("/create")
    public String createTeam(@ModelAttribute Team team,
                             @RequestParam Long tournamentId){
        Tournament tournament = service.getTournamentById(tournamentId);
        team.setTournament(tournament);
        service.createTeam(team);
        return "redirect:/teams?tournamentId=" + tournamentId;
    }

    // View team details
    @GetMapping("/{id}")
    public String viewTeam(@PathVariable Long id, Model model) {
        Team team = service.getTeamById(id);
        model.addAttribute("team", team);
        return "view_team"; // view_team.html
    }
}
