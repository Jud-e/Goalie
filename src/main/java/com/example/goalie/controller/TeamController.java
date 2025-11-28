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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final AppService service;

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
        boolean isUserInTeam = teams.stream()
                .flatMap(team -> team.getPlayerTeams().stream())
                .anyMatch(pt -> pt.getUser().equals(loggedInUser));
        model.addAttribute("user", loggedInUser);
//        model.addAttribute("userId", loggedInUser.getId()); // so template can use it
        model.addAttribute("tournament", tournament);
        model.addAttribute("teams", teams);
//        model.addAttribute("teamMemberIds", teamMemberIds);
        model.addAttribute("isUserInTeam", isUserInTeam);
        model.addAttribute("allowedSizes", List.of(4,8,16,32));

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
        // Save the team
        Team savedTeam = service.createTeam(team);
        // Automatically join creator to this team
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setTeam(savedTeam);
        playerTeam.setUser(user);
        service.createTeamAndJoin(user, savedTeam);
        service.addUserToTeam(playerTeam); // Only this is necessary
        return "redirect:/teams?tournamentId=" + tournamentId + "&success=Team created successfully";
    }



    // View team details
    @GetMapping("/view/{id}")
    public String viewTeam(@PathVariable Long id, Model model) {
        Team team = service.getTeamById(id);
        if (team == null) {
            return "redirect:/teams";
        }

        model.addAttribute("team", team);
        model.addAttribute("allowedSizes", List.of(4,8,16,32));
        return "viewTeams"; // Make sure you have view_team.html
    }

    @PostMapping("/join-random")
    public String joinRandomTeam(@RequestParam Long tournamentId,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to join a team");
            return "redirect:/login";
        }

        User user = service.getUserByEmail(principal.getName());
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/login";
        }

        Tournament tournament = service.getTournamentById(tournamentId);
        if (tournament == null) {
            redirectAttributes.addFlashAttribute("error", "Tournament not found");
            return "redirect:/tournaments";
        }

        // Check if user is premium
        if (service.isPremiumUser(user)) {
            redirectAttributes.addFlashAttribute("error",
                    "Premium users can select teams. Please choose a team to join.");
            return "redirect:/teams?tournamentId=" + tournamentId;
        }

        // Try to join a random team
        boolean joined = service.joinRandomTeam(user, tournament);

        if (joined) {
            Team userTeam = service.getUserTeamForTournament(user, tournament);
            redirectAttributes.addFlashAttribute("success",
                    "Successfully joined team: " + (userTeam != null ? userTeam.getName() : "a team"));
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Could not join a team. You may already be in a team or no teams are available.");
        }

        return "redirect:/teams?tournamentId=" + tournamentId;
    }


}
