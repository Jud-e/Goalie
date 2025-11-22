package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import com.example.goalie.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final AppService service;

    // List all teams
    @GetMapping
    public String viewTeams(@RequestParam Long tournamentId, Model model, HttpSession session){
        System.out.println("Received Tournament ID: " + tournamentId);
        Tournament tournament = service.getTournamentById(tournamentId);
        // 2. Check the result before it crashes
        System.out.println("Tournament Object Retrieved: " + tournament);
        if (tournament == null) {            // Redirect to a safe page (e.g., the tournament list)             // You could also add a message here using RedirectAttributes
             return "redirect:/tournaments";
        }
        // Get logged in user
        User user = (User) session.getAttribute("loggedInUser");
        List<Team> teams = service.getTeamsByTournament(tournament);        
        // Check if user is already in a team for this tournament
        boolean isUserInTeam = false;
        if (user != null) {
            isUserInTeam = service.isUserInTeamForTournament(user, tournament);
        }
        model.addAttribute("tournament", tournament);
        model.addAttribute("teams", teams);
        model.addAttribute("user", user);
        model.addAttribute("isUserInTeam", isUserInTeam);
        return "viewTeams";
    }

    // Show create team form
    @GetMapping("/create")
    public String showCreateTeamForm(@RequestParam Long tournamentId,
                                     Model model,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes){        // Check if user is logged in
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Please login to create a team");
            return "redirect:/login";
        }
        // Validate tournament exists
        Tournament tournament = service.getTournamentById(tournamentId);
        if (tournament == null) {
            redirectAttributes.addFlashAttribute("error", "Tournament not found");
            return "redirect:/tournaments";
        }
        model.addAttribute("team", new Team());
        model.addAttribute("tournament", tournament);
        return "createTeam";
    }
    // Handle team creation
    @PostMapping("/create")
    public String createTeam(@ModelAttribute Team team,
                             @RequestParam Long tournamentId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes){        // Check if user is logged in
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to create a team");
            return "redirect:/login";
        }
        // Validate tournament exists
        Tournament tournament = service.getTournamentById(tournamentId);
        if (tournament == null) {
            redirectAttributes.addFlashAttribute("error", "Tournament not found");
            return "redirect:/tournaments";
        }
        // Validate team name
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Team name is required");
            return "redirect:/teams/create?tournamentId=" + tournamentId;
        }
        // Check if team name already exists for this tournament
        List<Team> existingTeams = service.getTeamsByTournament(tournament);
        for (Team existingTeam : existingTeams) {
            if (existingTeam.getName() != null && existingTeam.getName().equalsIgnoreCase(team.getName().trim())) {
                redirectAttributes.addFlashAttribute("error", "A team with this name already exists in this tournament");
                return "redirect:/teams/create?tournamentId=" + tournamentId;
            }
        }
        // Set tournament and create team
        team.setTournament(tournament);
        team.setName(team.getName().trim()); // Clean up whitespace
        service.createTeam(team);        
        redirectAttributes.addFlashAttribute("success", "Team '" + team.getName() + "' created successfully!");
        return "redirect:/teams?tournamentId=" + tournamentId;
    }
    // View team details
    @GetMapping("/{id}")
    public String viewTeam(@PathVariable Long id, Model model) {
        Team team = service.getTeamById(id);
        model.addAttribute("team", team);
        return "view_team"; // view_team.html
        //
        }
    // Join a specific team (for premium users)
    @PostMapping("/join")
    public String joinTeam(@RequestParam Long teamId,
                           @RequestParam Long tournamentId,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to join a team");
            return "redirect:/login";
        }
        Team team = service.getTeamById(teamId);
        if (team == null) {
            redirectAttributes.addFlashAttribute("error", "Team not found");
            return "redirect:/teams?tournamentId=" + tournamentId;
        }
        // Check if user is premium
        boolean isPremium = service.isPremiumUser(user);
        if (!isPremium) {
            redirectAttributes.addFlashAttribute("error", "Only premium users can select teams. Regular users are assigned randomly.");
            return "redirect:/teams?tournamentId=" + tournamentId;
        }
        // Attempt to join the team
        boolean success = service.joinTeam(user, team);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Successfully joined team: " + team.getName());
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not join team. You may already be in a team for this tournament.");
        }
        return "redirect:/teams?tournamentId=" + tournamentId;
    }
    // Join a random team (for regular users)
    @PostMapping("/join-random")
    public String joinRandomTeam(@RequestParam Long tournamentId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to join a team");
            return "redirect:/login";
        }
        Tournament tournament = service.getTournamentById(tournamentId);
        if (tournament == null) {
            redirectAttributes.addFlashAttribute("error", "Tournament not found");
            return "redirect:/tournaments";
        }
        // Check if user is premium
        boolean isPremium = service.isPremiumUser(user);
        if (isPremium) {
            redirectAttributes.addFlashAttribute("error", "Premium users can select teams. Please choose a team to join.");
            return "redirect:/teams?tournamentId=" + tournamentId;
        }
        // Attempt to join a random team
        boolean success = service.joinRandomTeam(user, tournament);
        if (success) {
            Team userTeam = service.getUserTeamForTournament(user, tournament);
            redirectAttributes.addFlashAttribute("success", "Successfully joined team: " + (userTeam != null ? userTeam.getName() : "a team"));
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not join a team. You may already be in a team or no teams are available.");
        }
        return "redirect:/teams?tournamentId=" + tournamentId;    }}
