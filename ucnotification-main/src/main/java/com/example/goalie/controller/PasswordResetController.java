package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final AppService service;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password"; // your Thymeleaf template
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {
        User user = service.getUserByEmail(email);
        if (user != null) {
            String token = service.createPasswordResetToken(user);
            // send email here
            System.out.println("Reset link: http://localhost:8080/auth/reset-password?token=" + token);
        }
        model.addAttribute("message", "If an account with this email exists, a reset link has been sent.");
        return "forgot_password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam String token, Model model) {
        if (!service.validatePasswordResetToken(token)) {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset_password";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String token,
                                      @RequestParam String newPassword,
                                      Model model) {
        if (!service.validatePasswordResetToken(token)) {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset_password";
        }
        User user = service.getUserByPasswordResetToken(token);
        service.updatePassword(user, newPassword);
        
        // Create a notification for the user
        service.createNotification(user, "Your password was successfully reset.");
        
        model.addAttribute("message", "Password reset successfully. You can now login.");
        return "login";
    }
}
