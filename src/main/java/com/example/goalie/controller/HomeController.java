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
    public String home(HttpSession session, Model model, @RequestParam(required = false) String payment) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null) {
            return "redirect:/login";
        }
        
        // Get current plan from user subscription status
        String currentPlan = user.isSubscription() ? "premium" : "basic";
        session.setAttribute("currentPlan", currentPlan);
        
        // Check if payment form should be shown
        Boolean showPaymentForm = (Boolean) session.getAttribute("showPaymentForm");
        if (showPaymentForm != null && showPaymentForm) {
            session.removeAttribute("showPaymentForm");
        } else {
            showPaymentForm = false;
        }
        
        // Get user statistics
        int tournamentCount = service.getAllTournaments().size();
        
        // Check if player profile exists (for first-time premium setup)
        boolean hasPlayerProfile = service.hasPlayerProfile(user);
        boolean showPlayerSetup = false;
        
        if ("premium".equals(currentPlan) && !hasPlayerProfile) {
            showPlayerSetup = true;
        }
        
        // Payment success/error messages
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
        model.addAttribute("showPaymentForm", showPaymentForm != null ? showPaymentForm : false);
        return "home";
    }
    
    // Switch plan or show payment form
    @PostMapping("/switch-plan")
    public String switchPlan(@RequestParam String plan, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null) {
            return "redirect:/login";
        }
        
        if ("basic".equals(plan)) {
            // Basic plan is always available
            session.setAttribute("currentPlan", "basic");
            return "redirect:/home";
        } else if ("premium".equals(plan)) {
            // Check if user already has premium
            if (user.isSubscription()) {
                session.setAttribute("currentPlan", "premium");
                return "redirect:/home";
            } else {
                // Show payment form by setting session attribute
                session.setAttribute("showPaymentForm", true);
                return "redirect:/home";
            }
        }
        
        return "redirect:/home";
    }
    
    // Process premium payment
    @PostMapping("/upgrade-premium")
    public String upgradePremium(@RequestParam String cardNumber,
                                 @RequestParam String cardHolder,
                                 @RequestParam String expiryDate,
                                 @RequestParam String cvv,
                                 HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null) {
            return "redirect:/login";
        }
        
        // TODO: Integrate with actual payment gateway (Stripe, PayPal, etc.)
        // For now, we'll simulate successful payment
        
        // Validate payment details (basic validation)
        if (cardNumber == null || cardNumber.trim().isEmpty() ||
            cardHolder == null || cardHolder.trim().isEmpty() ||
            expiryDate == null || expiryDate.trim().isEmpty() ||
            cvv == null || cvv.trim().isEmpty()) {
            return "redirect:/home?payment=error";
        }
        
        // Simulate payment processing
        // In production, this would call a payment gateway API
        user.setSubscription(true);
        service.saveUser(user);
        
        // Update session
        session.setAttribute("loggedInUser", user);
        session.setAttribute("currentPlan", "premium");
        
        return "redirect:/home?payment=success";
    }
    
    // Save player profile setup
    @PostMapping("/setup-player-profile")
    public String setupPlayerProfile(@RequestParam String playerNickname,
                                     @RequestParam Integer skillRating,
                                     @RequestParam(required = false) String preferredPosition,
                                     @RequestParam(required = false) String dominantFoot,
                                     @RequestParam(required = false) String bio,
                                     HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null) {
            return "redirect:/login";
        }
        
        service.createOrUpdatePlayerProfile(user, playerNickname, skillRating, 
                                           preferredPosition, dominantFoot, bio);
        
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
