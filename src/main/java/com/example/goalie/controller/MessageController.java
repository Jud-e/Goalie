package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Message;
import com.example.goalie.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final AppService service;

    // Show inbox with list of conversations
    @GetMapping
    public String inbox(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Message> allMessages = service.getMessagesForUser(user);

        // Group messages by conversation partner
        Map<Long, User> conversationPartners = new HashMap<>();
        Map<Long, Message> lastMessages = new HashMap<>();

        for (Message message : allMessages) {
            User partner = message.getSender().getId().equals(user.getId())
                    ? message.getReceiver()
                    : message.getSender();

            Long partnerId = partner.getId();
            conversationPartners.put(partnerId, partner);

            // Track the most recent message with each partner
            if (!lastMessages.containsKey(partnerId) ||
                    message.getTimestamp().after(lastMessages.get(partnerId).getTimestamp())) {
                lastMessages.put(partnerId, message);
            }
        }

        model.addAttribute("conversationPartners", conversationPartners.values());
        model.addAttribute("lastMessages", lastMessages);
        model.addAttribute("user", user);
        return "messages";
    }

    // Start a new conversation or view existing one
    @GetMapping("/new")
    public String newMessage(@RequestParam(required = false) Long userId,
                             HttpSession session,
                             Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (userId != null) {
            User otherUser = service.getUserById(userId);
            if (otherUser != null && !otherUser.getId().equals(user.getId())) {
                return "redirect:/messages/conversation/" + userId;
            }
        }

        // Get all users for starting a new conversation
        List<User> allUsers = service.getAllUsers();
        allUsers.removeIf(u -> u.getId().equals(user.getId()));
        model.addAttribute("users", allUsers);
        model.addAttribute("user", user);
        return "new_message";
    }

    // View conversation with a specific user
    @GetMapping("/conversation/{userId}")
    public String conversation(@PathVariable Long userId,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        User otherUser = service.getUserById(userId);
        if (otherUser == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/messages";
        }

        if (otherUser.getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Cannot message yourself");
            return "redirect:/messages";
        }

        List<Message> conversation = service.getConversation(user, otherUser);
        Collections.sort(conversation, (m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        model.addAttribute("conversation", conversation);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("user", user);
        return "conversation";
    }

    // Send a message
    @PostMapping("/send")
    public String sendMessage(@RequestParam Long receiverId,
                              @RequestParam String content,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User sender = (User) session.getAttribute("loggedInUser");
        if (sender == null) {
            return "redirect:/login";
        }

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Message content cannot be empty");
            return "redirect:/messages/conversation/" + receiverId;
        }

        User receiver = service.getUserById(receiverId);
        if (receiver == null) {
            redirectAttributes.addFlashAttribute("error", "Receiver not found");
            return "redirect:/messages";
        }

        if (receiver.getId().equals(sender.getId())) {
            redirectAttributes.addFlashAttribute("error", "Cannot send message to yourself");
            return "redirect:/messages";
        }

        try {
            service.sendMessage(sender, receiver, content.trim());
            redirectAttributes.addFlashAttribute("success", "Message sent successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send message: " + e.getMessage());
        }

        return "redirect:/messages/conversation/" + receiverId;
    }

    // Delete a message
    @PostMapping("/delete/{id}")
    public String deleteMessage(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Message message = service.getMessageById(id);
        if (message == null) {
            redirectAttributes.addFlashAttribute("error", "Message not found");
            return "redirect:/messages";
        }

        // Verify the message belongs to the user (either sender or receiver)
        if (!message.getSender().getId().equals(user.getId()) &&
                !message.getReceiver().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to delete this message");
            return "redirect:/messages";
        }

        Long conversationPartnerId = message.getSender().getId().equals(user.getId())
                ? message.getReceiver().getId()
                : message.getSender().getId();

        service.deleteMessage(id);
        redirectAttributes.addFlashAttribute("success", "Message deleted successfully");
        return "redirect:/messages/conversation/" + conversationPartnerId;
    }
}
