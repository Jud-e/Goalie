package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.goalieEnum.DominantFoot;
import com.example.goalie.goalieEnum.Position;
import com.example.goalie.model.User;
import com.example.goalie.model.UserProfile;
import com.example.goalie.repository.UserProfileRepository;
import com.example.goalie.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

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

    @GetMapping("/profile_account")
    public String getProfile(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isEmpty()) return "redirect:/login";

        User user = optionalUser.get();
        model.addAttribute("user", user);

        Optional<UserProfile> optionalProfile = userProfileRepository.findByUser(user);
        UserProfile profile = optionalProfile.orElse(new UserProfile()); // fallback
        model.addAttribute("userProfile", profile);

        return "profile_account"; // your Thymeleaf template
    }


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @PostMapping("/edit_profile")
    public String editProfile(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String playerNickname,
            @RequestParam(required = false) Integer skillRating,
            @RequestParam(required = false) String preferredPosition,
            @RequestParam(required = false) String secondaryPosition,
            @RequestParam(required = false) String dominantFoot,
            @RequestParam(required = false) String bio,
            Principal principal) {

        if (principal == null) return "redirect:/login";

        // Update User
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isEmpty()) return "redirect:/login";
        User user = optionalUser.get();

        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);

        // Update or create UserProfile
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setPlayerNickname(playerNickname);
        profile.setSkillRating(skillRating);

        if (preferredPosition != null) {
            profile.setPreferredPosition(Position.valueOf(preferredPosition));
        }
        if (secondaryPosition != null) {
            profile.setSecondaryPosition(Position.valueOf(secondaryPosition));
        }
        if (dominantFoot != null) {
            profile.setDominantFoot(DominantFoot.valueOf(dominantFoot));
        }
        profile.setBio(bio);
        userProfileRepository.save(profile);

        return "redirect:/home";
    }


}
