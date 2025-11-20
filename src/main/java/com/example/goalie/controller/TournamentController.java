package com.example.goalie.controller;

import com.example.goalie.model.Tournament;
import com.example.goalie.service.TournamentService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
public class TournamentController {
    
    private final TournamentService tournamentService;
    
    @GetMapping("/tournaments")
    public String tournaments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
        List<Tournament> tournaments;
        
        // Check if any filter or search is applied
        boolean hasFilters = (search != null && !search.trim().isEmpty()) ||
                            (status != null && !status.trim().isEmpty()) ||
                            (sport != null && !sport.trim().isEmpty()) ||
                            (location != null && !location.trim().isEmpty()) ||
                            startDate != null ||
                            endDate != null;
        
        if (hasFilters) {
            tournaments = tournamentService.searchAndFilterTournaments(
                search, status, sport, location, startDate, endDate
            );
        } else {
            tournaments = tournamentService.getAllTournaments();
        }
        
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("sports", tournamentService.getAllSports());
        model.addAttribute("locations", tournamentService.getAllLocations());
        model.addAttribute("statuses", Tournament.TournamentStatus.values());
        model.addAttribute("currentSearch", search != null ? search : "");
        model.addAttribute("currentStatus", status != null ? status : "");
        model.addAttribute("currentSport", sport != null ? sport : "");
        model.addAttribute("currentLocation", location != null ? location : "");
        model.addAttribute("currentStartDate", startDate != null ? startDate.toString() : "");
        model.addAttribute("currentEndDate", endDate != null ? endDate.toString() : "");
        
        return "tournaments";
    }
}

