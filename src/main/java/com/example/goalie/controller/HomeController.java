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
    public String home(HttpSession session){
        // Auto-login with premium user if not already logged in
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            // Auto-login with premium user (uc@test.com)
            User autoUser = service.getUserByEmail("uc@test.com");
            if (autoUser != null) {
                session.setAttribute("loggedInUser", autoUser);
                return "redirect:/home";
            } else {
                // Fallback to signup if test user doesn't exist
                return "redirect:/signup";
            }
        }
        return "redirect:/home";
    }//signup functions
    @GetMapping("/signup")
    public String showSignup(Model model, HttpSession session){
        // Auto-login and redirect if not logged in
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            User autoUser = service.getUserByEmail("uc@test.com");
            if (autoUser != null) {
                session.setAttribute("loggedInUser", autoUser);
                return "redirect:/home";
            }
        } else {
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "signup";
    }
    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("user")  User user, BindingResult br, Model model){
        // Validate required fields
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            br.rejectValue("email", "error.user", "Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            br.rejectValue("password", "error.user", "Password is required");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            br.rejectValue("name", "error.user", "Name is required");
        }
        // Check if email already exists
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && service.emailExists(user.getEmail())) {
            br.rejectValue("email", "error.user", "A user with this email already exists");
        }
        if (br.hasErrors()){
            return "signup";
        }
        try {
            user.setSubscription(false);
            user.setTournament(null);
            // user.setPassword(passwordEncoder.encode(user.getPassword()));
            service.createUser(user);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }
    //login functions
    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session){
        // Auto-login and redirect if not logged in
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            User autoUser = service.getUserByEmail("uc@test.com");
            if (autoUser != null) {
                session.setAttribute("loggedInUser", autoUser);
                return "redirect:/home";
            }
        } else {
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "login";
    }
    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute("user")  User user, Model model, HttpSession session){
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
        User userExisting = service.getUserByEmail(user.getEmail());
        // Check if user exists and password matches
        if (userExisting != null && userExisting.getPassword() != null && userExisting.getPassword().equals(user.getPassword())){
            session.setAttribute("loggedInUser",userExisting);
            return "redirect:/home";
        }
        else {
            model.addAttribute("error", "Invalid email or password");
            model.addAttribute("user", new User());
            return "login";
        }
    }//homepage
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null) {
            // Auto-login with premium user if not logged in
            User autoUser = service.getUserByEmail("uc@test.com");
            if (autoUser != null) {
                session.setAttribute("loggedInUser", autoUser);
                user = autoUser;}
            else {
                return "redirect:/login";
            }
        }
        model.addAttribute("user", user);
        return "home";
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
