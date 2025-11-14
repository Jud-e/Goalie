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
    public String listTeams(Model model) {
        List<Team> teams = service.getAllTeams();
        model.addAttribute("teams", teams);
        return "teams"; // teams.html
    }

    // Show create team form
    @GetMapping("/create")
    public String showCreateTeamForm(Model model) {
        model.addAttribute("team", new Team());
        model.addAttribute("tournaments", service.getAllTournaments());
        return "create_team"; // create_team.html
    }

    // Handle team creation
    @PostMapping("/create")
    public String createTeam(@ModelAttribute Team team,
                             @RequestParam Long tournamentId) {
        Tournament tournament = service.getTournamentById(tournamentId);
        team.setTournament(tournament);
        service.createTeam(team);
        return "redirect:/teams";
    }

    // View team details
    @GetMapping("/{id}")
    public String viewTeam(@PathVariable Long id, Model model) {
        Team team = service.getTeamById(id);
        model.addAttribute("team", team);
        return "view_team"; // view_team.html
    }
}
