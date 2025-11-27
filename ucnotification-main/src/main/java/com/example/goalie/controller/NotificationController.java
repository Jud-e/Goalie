package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Notification;
import com.example.goalie.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final AppService service;

    // List notifications for logged-in user
    @GetMapping
    public String viewNotifications(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = service.getUserByEmail(email);
        
        if (user == null) {
            return "redirect:/login";
        }

        List<Notification> notifications = service.getNotificationsByUser(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("user", user);
        return "notifications";
    }

    // Delete a notification
    @PostMapping("/delete/{id}")
    public String deleteNotification(@PathVariable Long id,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = service.getUserByEmail(email);
        
        if (user == null) {
            return "redirect:/login";
        }

        Notification notification = service.getNotificationById(id);
        if (notification == null) {
            redirectAttributes.addFlashAttribute("error", "Notification not found");
            return "redirect:/notifications";
        }

        // Verify the notification belongs to the user
        if (!notification.getReceiver().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to delete this notification");
            return "redirect:/notifications";
        }

        service.deleteNotification(id);
        redirectAttributes.addFlashAttribute("success", "Notification deleted successfully");
        return "redirect:/notifications";
    }

    // Delete all notifications for user
    @PostMapping("/delete-all")
    public String deleteAllNotifications(Principal principal,
                                         RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = service.getUserByEmail(email);
        
        if (user == null) {
            return "redirect:/login";
        }

        service.deleteAllNotificationsForUser(user);
        redirectAttributes.addFlashAttribute("success", "All notifications deleted successfully");
        return "redirect:/notifications";
    }
}
