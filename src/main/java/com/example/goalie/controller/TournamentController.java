package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Tournament;
import com.example.goalie.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final AppService service;

    // 1️⃣ List all tournaments
    @GetMapping
    public String listTournaments(Model model) {
        List<Tournament> tournaments = service.getAllTournaments();
        model.addAttribute("tournaments", tournaments);
        return "tournaments"; // templates/tournaments.html
    }

    // 2️⃣ Show create tournament form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("tournament", new Tournament());
        return "create_tournament"; // templates/create_tournament.html
    }

    // 3️⃣ Handle tournament creation
    @PostMapping("/create")
    public String createTournament(@ModelAttribute Tournament tournament,
                                   HttpSession session) {
        User organizer = (User) session.getAttribute("loggedInUser");
        if (organizer == null) return "redirect:/login";
        tournament.setStatus("Upcoming");
        service.createTournament(tournament, organizer);
        return "redirect:/tournaments"; // back to tournament list
    }

    // 4️⃣ View tournament details
    @GetMapping("/{id}")
    public String viewTournament(@PathVariable Long id, Model model) {
        Tournament tournament = service.getTournamentById(id);
        model.addAttribute("tournament", tournament);
        return "view_tournament"; // templates/view_tournament.html
    }
}





//package com.example.goalie.controller;
//
//import com.example.goalie.config.AppService;
//import com.example.goalie.model.Tournament;
//import com.example.goalie.model.User;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/tournaments")
//@RequiredArgsConstructor
//public class TournamentController {
//
//    private final AppService service;
//
//    // Show all tournaments
//    @GetMapping
//    public String listTournaments(Model model) {
//        List<Tournament> tournaments = service.getAllTournaments();
//        model.addAttribute("tournaments", tournaments);
//        return "tournaments"; // tournaments.html
//    }
//
////     Show create tournament form
//    @GetMapping("/create")
//    public String showCreateForm(Model model) {
//        model.addAttribute("tournament", new Tournament());
//        return "create_tournament"; // create_tournament.html
//    }
//
//    // Handle tournament creation
//    @PostMapping("/create_tournament")
//    public String createTournament(@ModelAttribute Tournament tournament,
//                                   HttpSession session) {
//        User organizer = (User) session.getAttribute("loggedInUser");
//        if (organizer == null) return "redirect:/login";
//
//        tournament.setStatus("Upcoming");
//        service.createTournament(tournament, organizer);
//        return "redirect:/tournaments";
//    }
//
//    // View tournament details
//    @GetMapping("/{id}")
//    public String viewTournament(@PathVariable Long id, Model model) {
//        Tournament tournament = service.getTournamentById(id);
//        model.addAttribute("tournament", tournament);
//        return "view_tournament"; // view_tournament.html
//    }
//}
