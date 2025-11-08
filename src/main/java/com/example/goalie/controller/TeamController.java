package com.example.goalie.controller;


import com.example.goalie.model.Team;
import com.example.goalie.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class TeamController {
    private final AppService appService;

    @GetMapping("/createTeam")
    public String createTeam(Model model){
        model.addAttribute("team", new Team());
        return "createTeam";
    }
}
