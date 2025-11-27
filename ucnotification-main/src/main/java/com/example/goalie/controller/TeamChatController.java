package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Team;
import com.example.goalie.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teams/chat")
public class TeamChatController {
    private final AppService service;

    private boolean isMember(Team team, User user) {
        return team.getPlayers().stream()
                .anyMatch(p -> p.getId().equals(user.getId()));
    }

    @GetMapping("/{teamId}")
    public String viewChat(@PathVariable Long teamId,
                           @AuthenticationPrincipal User currentUser,
                           Model model) {

        Team team = service.getTeamById(teamId);

        // Security check: must be a member of the team
        if (!isMember(team, currentUser)) {
            return "redirect:/forbidden";
        }

        model.addAttribute("team", team);
        model.addAttribute("messages", service.getTeamMessages(team));

        return "teams/chat";
    }

    @PostMapping("/send")
    public String sendMessage(@AuthenticationPrincipal User sender,
                              @RequestParam Long teamId,
                              @RequestParam String content) {

        Team team = service.getTeamById(teamId);

        if (isMember(team, sender)) {
            service.sendMessage(team, sender, content);
        }

        return "redirect:/teams/chat/" + teamId;
    }
}
