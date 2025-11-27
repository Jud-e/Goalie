package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.User;
import com.example.goalie.model.UserProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class HomeController {

    private final AppService service;
    private final BCryptPasswordEncoder passwordEncoder;

    // Home redirects to signup if no user
    @GetMapping("/")
    public String root() {
        return "redirect:/signup";
    }

    // Signup page
    @GetMapping("/signup")
    public String showSignup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // Signup submission
    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("user") User user, BindingResult br, Model model) {
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

        // Check if email exists
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && service.emailExists(user.getEmail())) {
            br.rejectValue("email", "error.user", "A user with this email already exists");
        }

        if (br.hasErrors()) {
            model.addAttribute("user", user);
            return "signup";
        }

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setSubscription(false);
        service.createUser(user);

        return "redirect:/login?signup=success";
    }

    // Login page (GET only)
    @GetMapping("/login")
    public String showLogin(Model model, @RequestParam(required = false) String signup) {
        model.addAttribute("user", new User());
        if ("success".equals(signup)) {
            model.addAttribute("success", "Account created successfully! Please login.");
        }
        return "login";
    }

    // Home page
    @GetMapping("/home")
    public String home(Model model, Principal principal, HttpSession session,
                       @RequestParam(required = false) String payment) {

        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = service.getUserByEmail(email);

        // Current plan
        String currentPlan = user.isSubscription() ? "premium" : "basic";
        session.setAttribute("currentPlan", currentPlan);

        // Payment form toggle
        Boolean showPaymentForm = (Boolean) session.getAttribute("showPaymentForm");
        if (showPaymentForm != null && showPaymentForm) {
            session.removeAttribute("showPaymentForm");
        } else {
            showPaymentForm = false;
        }

        // User statistics
        int tournamentCount = service.getAllTournaments().size();

        // Player profile check
        boolean hasPlayerProfile = service.hasPlayerProfile(user);
        boolean showPlayerSetup = "premium".equals(currentPlan) && !hasPlayerProfile;

        // Payment messages
        if ("success".equals(payment)) {
            model.addAttribute("paymentSuccess", "Payment successful! Welcome to Premium!");
        } else if ("error".equals(payment)) {
            model.addAttribute("paymentError", "Payment failed. Please try again.");
        }

        model.addAttribute("user", user);
        model.addAttribute("currentPlan", currentPlan);
        model.addAttribute("isPremium", user.isSubscription());
        model.addAttribute("tournamentCount", tournamentCount);
        model.addAttribute("showPlayerSetup", showPlayerSetup);
        model.addAttribute("showPaymentForm", showPaymentForm);

        return "home";
    }

    // Switch plan or show payment form
    @PostMapping("/switch-plan")
    public String switchPlan(@RequestParam String plan, Principal principal, HttpSession session) {
        if (principal == null) return "redirect:/login";

        User user = service.getUserByEmail(principal.getName());

        if ("basic".equals(plan)) {
            session.setAttribute("currentPlan", "basic");
        } else if ("premium".equals(plan)) {
            if (user.isSubscription()) {
                session.setAttribute("currentPlan", "premium");
            } else {
                session.setAttribute("showPaymentForm", true);
            }
        }

        return "redirect:/home";
    }

    // Upgrade to premium
    @PostMapping("/upgrade-premium")
    public String upgradePremium(@RequestParam String cardNumber,
                                 @RequestParam String cardHolder,
                                 @RequestParam String expiryDate,
                                 @RequestParam String cvv,
                                 Principal principal, HttpSession session) {
        if (principal == null) return "redirect:/login";

        User user = service.getUserByEmail(principal.getName());

        if (cardNumber == null || cardNumber.trim().isEmpty() ||
                cardHolder == null || cardHolder.trim().isEmpty() ||
                expiryDate == null || expiryDate.trim().isEmpty() ||
                cvv == null || cvv.trim().isEmpty()) {
            return "redirect:/home?payment=error";
        }

        user.setSubscription(true);
        service.saveUser(user);

        session.setAttribute("currentPlan", "premium");

        return "redirect:/home?payment=success";
    }

    // Player profile setup
    @PostMapping("/setup-player-profile")
    public String setupPlayerProfile(@RequestParam String playerNickname,
                                     @RequestParam Integer skillRating,
                                     @RequestParam(required = false) String preferredPosition,
                                     @RequestParam(required = false) String dominantFoot,
                                     @RequestParam(required = false) String bio,
                                     Principal principal) {

        if (principal == null) return "redirect:/login";

        User user = service.getUserByEmail(principal.getName());
        service.createOrUpdatePlayerProfile(user, playerNickname, skillRating, preferredPosition, dominantFoot, bio);

        return "redirect:/home";
    }

    // Static pages
    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/profile_account")
    public String profileAccount(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = service.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        return "profile_account";
    }

    @PostMapping("/edit_profile")
    public String editProfile(@ModelAttribute("user") User updatedUser, Principal principal) {
        if (principal == null) return "redirect:/login";

        User userExisting = service.getUserByEmail(principal.getName());
        userExisting.setName(updatedUser.getName());
        userExisting.setEmail(updatedUser.getEmail());
        service.saveUser(userExisting);

        return "redirect:/home";
    }
}
