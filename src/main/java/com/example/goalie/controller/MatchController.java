package com.example.goalie.controller;


import com.example.goalie.config.AppService;
import com.example.goalie.model.Match;
import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {

    private final AppService service;

    @GetMapping("/{id}")
    public String viewMatches(@PathVariable Long id, Model model) {

        Tournament tournament = service.getTournamentById(id);
        List<Match> matches = service.getMatchesByTournament(tournament);
        List<Team> teams = service.getTeamsByTournament(tournament);

        model.addAttribute("matches", matches);
        model.addAttribute("tournament", tournament);
        model.addAttribute("teams", teams);

        return "view_matches";
    }


}