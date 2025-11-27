package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Message;
import com.example.goalie.model.Team;
import com.example.goalie.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AppService service;

    @GetMapping("/{teamId}")
    public String viewChat(@PathVariable Long teamId, Model model, HttpSession session) {

        Team team = service.getTeamById(teamId);
        if (team == null) {
            return "redirect:/teams";
        }

        List<Message> messages = service.getMessagesByTeam(team);

        model.addAttribute("team", team);
        model.addAttribute("messages", messages);
        model.addAttribute("messageForm", new Message());

        return "chat";
    }


    @PostMapping("/send")
    public String sendMessage(@ModelAttribute("messageForm") Message message,
                              @RequestParam Long teamId,
                              HttpSession session) {

        User sender = (User) session.getAttribute("user");
        Team team = service.getTeamById(teamId);

        if (sender == null || team == null) {
            return "redirect:/chat/" + teamId;
        }

        message.setSender(sender);
        message.setTeam(team);
        message.setTimestamp(LocalDateTime.now());
        service.saveMessage(message);

        return "redirect:/chat/" + teamId; // FIXED
    }

}
