package com.example.goalie.controller;
import com.example.goalie.config.AppService;
import com.example.goalie.model.Tournament;
import com.example.goalie.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {
    private final AppService service;

    @GetMapping("/")
    public String home(){
        return "redirect:/signup";
//        return "home";
    }
//signup functions
    @GetMapping("/signup")
    public String showSignup(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }
    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("user") User user, BindingResult br, Model model){
        // Validate required fields
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            br.rejectValue("email", "error.user", "Email is required");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            br.rejectValue("name", "error.user", "Username is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            br.rejectValue("password", "error.user", "Password is required");
        }
        
        // Check if email already exists
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && service.emailExists(user.getEmail())) {
            br.rejectValue("email", "error.user", "A user with this email already exists");
        }
        
        if (br.hasErrors()){
            model.addAttribute("user", user);
            return "signup";
        }
        
        try {
            user.setSubscription(false);
            user.setTournament(null);
            service.createUser(user);
            return "redirect:/login?signup=success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "signup";
        }
    }
//login functions
    @GetMapping("/login")
    public String showLogin(Model model, @RequestParam(required = false) String signup){
        model.addAttribute("user", new User());
        if ("success".equals(signup)) {
            model.addAttribute("success", "Account created successfully! Please login.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute("user") User user, Model model, HttpSession session){
        // Validate input
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "Email is required");
            model.addAttribute("user", new User());
            return "login";
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "Password is required");
            model.addAttribute("user", new User());
            return "login";
        }
        
        // Get user by email
        User userExisting = service.getUserByEmail(user.getEmail());
        
        if (userExisting != null && userExisting.getPassword() != null && 
            userExisting.getPassword().equals(user.getPassword())){
            session.setAttribute("loggedInUser", userExisting);
            return "redirect:/home";
        }
        else {
            model.addAttribute("error", "Invalid email or password");
            model.addAttribute("user", new User());
            return "login";
        }
    }
//homepage
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null) {
            return "redirect:/login";
        }
        
        // Get current mode from session, default to "user"
        String currentMode = (String) session.getAttribute("userMode");
        if (currentMode == null) {
            currentMode = "user";
            session.setAttribute("userMode", currentMode);
        }
        
        // Get user statistics
        int tournamentCount = service.getAllTournaments().size();
        // You can add more stats here as needed
        
        model.addAttribute("user", user);
        model.addAttribute("currentMode", currentMode);
        model.addAttribute("tournamentCount", tournamentCount);
        return "home";
    }
    
    // Switch user mode
    @PostMapping("/switch-mode")
    public String switchMode(@RequestParam String mode, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null) {
            return "redirect:/login";
        }
        
        if ("user".equals(mode) || "player".equals(mode)) {
            session.setAttribute("userMode", mode);
        }
        
        return "redirect:/home";
    }





    @GetMapping("/about")
    public String About(){
        return "about";
    }

    @GetMapping("/faq")
    public String Faq(){
        return null;
    }

    @GetMapping("/profile_account")
    public String profileAccount(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // or show an error
        }
        model.addAttribute("user", user);
        return "profile_account";
    }

    @PostMapping("/edit_profile")
    public String editProfile(@ModelAttribute("user") User updatedUser, HttpSession session, BindingResult br){
        if (br.hasErrors()){
            return "profile_account";
        }
        User userExisting = (User) session.getAttribute("loggedInUser");
        userExisting.setName(updatedUser.getName());
        userExisting.setEmail(updatedUser.getEmail());
        service.saveUser(userExisting);
        // update session
        session.setAttribute("loggedInUser", userExisting);

        return "redirect:/home";
    }


}
