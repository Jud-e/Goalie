package com.example.goalie.controller;


import com.example.goalie.config.AppService;
import com.example.goalie.model.Match;
import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("allowedSizes", List.of(4,8,16,32));

        return "view_matches";

    }

    @GetMapping("/edit/{id}")
    public String showEditMatchForm(@PathVariable Long id, Model model) {

        Match match = service.getMatchById(id);
        if (match == null) {
            return "redirect:/tournaments";
        }
        model.addAttribute("match", match);
        return "edit_match";
    }

    @PostMapping("/edit/{id}")
    public String editMatch(@ModelAttribute Match match,
                            @PathVariable Long id,
                            BindingResult br,
                            RedirectAttributes ra) {

        Match existingMatch = service.getMatchById(id);



        if (existingMatch == null) {
            ra.addFlashAttribute("error", "Match not found");
            return "redirect:/tournaments";
        }

        Tournament tournament = existingMatch.getTournament();
        Long tournamentId = (tournament != null) ? tournament.getId() : null;

        if (br.hasErrors()) return "edit_match";

        if (match.getTeam1Score() == null || match.getTeam2Score() == null) {
            ra.addFlashAttribute("error", "Enter team One score and team One score.");
            return "redirect:/match/edit/" + id;
        }

        if (match.getTeam1Score() < 0 || match.getTeam2Score() < 0) {
            ra.addFlashAttribute("error", "Scores cannot be negative");
            return "redirect:/match/edit/" + id;
        }

        existingMatch.setTeam1Score(match.getTeam1Score());
        existingMatch.setTeam2Score(match.getTeam2Score());
        existingMatch.setMatchDate(match.getMatchDate());
        service.updateMatch(existingMatch);


        ra.addAttribute("success", "Match scores successfully updated");

        if (tournamentId != null) {
            return "redirect:/match/" + tournamentId;
        } else {
            return "redirect:/tournaments";
        }
//        return "redirect:/match/" + tid;

    }

}