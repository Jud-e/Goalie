package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.PlayerTeam;
import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import com.example.goalie.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final AppService service;

    // List all teams for a tournament
    @GetMapping
    public String viewTeams(@RequestParam Long tournamentId,
                            @RequestParam(required = false) String success,
                            Model model, Principal principal) {

        Tournament tournament = service.getTournamentById(tournamentId);
        if (tournament == null) {
            return "redirect:/tournaments";
        }

        List<Team> teams = service.getTeamsByTournament(tournament);
        User loggedInUser = service.getUserByEmail(principal.getName());
        model.addAttribute("user", loggedInUser);
        model.addAttribute("tournament", tournament);
        model.addAttribute("teams", teams);

        if (success != null && !success.isEmpty()) {
            model.addAttribute("success", success);
        }


        return "viewTeams";
    }

    // Show create team form
    @GetMapping("/create")
    public String showCreateTeamForm(@RequestParam Long tournamentId,
                                     Model model,
                                     Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = service.getUserByEmail(principal.getName());
        Tournament tournament = service.getTournamentById(tournamentId);
        if (tournament == null) {
            return "redirect:/tournaments";
        }

        model.addAttribute("team", new Team());
        model.addAttribute("tournament", tournament);
        model.addAttribute("user", user);
        return "createTeam";
    }

    // Handle team creation
    @PostMapping("/create")
    public String createTeam(@ModelAttribute Team team,
                             @RequestParam Long tournamentId,
                             Principal principal,
                             Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = service.getUserByEmail(principal.getName());
        Tournament tournament = service.getTournamentById(tournamentId);

        if (tournament == null) {
            return "redirect:/tournaments";
        }

        // Validate team name
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            model.addAttribute("error", "Team name is required");
            model.addAttribute("team", team);
            model.addAttribute("tournament", tournament);
            model.addAttribute("user", user);
            return "createTeam";
        }

        // Check for duplicate team name in this tournament
        List<Team> existingTeams = service.getTeamsByTournament(tournament);
        boolean nameExists = existingTeams.stream()
                .anyMatch(t -> t.getName() != null &&
                        t.getName().equalsIgnoreCase(team.getName().trim()));

        if (nameExists) {
            model.addAttribute("error", "A team with this name already exists in this tournament");
            model.addAttribute("team", team);
            model.addAttribute("tournament", tournament);
            model.addAttribute("user", user);
            return "createTeam";
        }

        // Set tournament for the team
        team.setTournament(tournament);

        // Ensure players list is initialized
        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }

        // Save the team first
        Team savedTeam = service.createTeam(team);

        // Create PlayerTeam link to add creator to the team
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setTeam(savedTeam);
        playerTeam.setUser(user);
        service.addUserToTeam(playerTeam);

        // Update the team entity to include the user
        savedTeam.getPlayers().add(user);
        service.createTeam(savedTeam); // persist the updated players list

        return "redirect:/teams?tournamentId=" + tournamentId + "&success=Team created successfully";
    }



    // View team details
    @GetMapping("/{id}")
    public String viewTeam(@PathVariable Long id, Model model) {
        Team team = service.getTeamById(id);
        if (team == null) {
            return "redirect:/teams";
        }

        model.addAttribute("team", team);
        return "viewTeams"; // Make sure you have view_team.html
    }
}
