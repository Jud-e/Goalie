package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.Message;
import com.example.goalie.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final AppService service;

    // Show inbox
    @GetMapping
    public String inbox(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<Message> messages = service.getMessagesForUser(user);
        model.addAttribute("messages", messages);
        return "messages"; // messages.html
    }

    // View conversation with a specific user
    @GetMapping("/conversation/{userId}")
    public String conversation(@PathVariable Long userId,
                               HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        User otherUser = service.getUserById(userId);
        List<Message> conversation = service.getConversation(user, otherUser);

        model.addAttribute("conversation", conversation);
        model.addAttribute("otherUser", otherUser);
        return "conversation"; // conversation.html
    }

    // Send a message
    @PostMapping("/send")
    public String sendMessage(@RequestParam Long receiverId,
                              @RequestParam String content,
                              HttpSession session) {
        User sender = (User) session.getAttribute("loggedInUser");
        if (sender == null) return "redirect:/login";

        User receiver = service.getUserById(receiverId);
        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        service.sendMessage(sender,receiver,content);
        return "redirect:/messages/conversation/" + receiverId;
    }
}
