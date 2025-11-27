package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Advertisement;
import com.example.goalie.model.PlayerTeam;
import com.example.goalie.model.Team;
import com.example.goalie.model.User;
import com.example.goalie.model.Tournament;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final AppService appService;

    public AdminController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {

        // Fetch all data from AppService
        List<User> users = appService.getAllUsers();
        List<Tournament> tournaments = appService.getAllTournaments();
        List<Team> teams = appService.getAllTeams();
        List<PlayerTeam> players = appService.getAllPlayers();
        List<Advertisement> ads = appService.getAllAdvertisements();

        // Create a Map<TeamId, List<PlayerTeam>> for Thymeleaf to iterate
        Map<Long, List<PlayerTeam>> teamPlayersMap = players.stream()
                .collect(Collectors.groupingBy(pt -> pt.getTeam().getId()));

        // Add attributes for Thymeleaf
        model.addAttribute("users", users);
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("teams", teams);
        model.addAttribute("teamPlayersMap", teamPlayersMap); // for Teams & Players section
        model.addAttribute("ads", ads);

        return "admin"; // admin.html
    }
}
