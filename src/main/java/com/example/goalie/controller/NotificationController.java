package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Notification;
import com.example.goalie.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final AppService service;

    // List notifications for logged-in user
    @GetMapping("/notifications")
    public String viewNotifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<Notification> notifications = service.getNotificationsByUser(user);
        model.addAttribute("notifications", notifications);
        return "notifications"; // notifications.html
    }
}
