package com.example.goalie.controller;
import com.example.goalie.config.AppService;
import com.example.goalie.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String signupSubmit(@ModelAttribute("user")  User user, BindingResult br){
        if (br.hasErrors()){
            return "signup";
        }
        user.setSubscription(false);
        user.setTournament(null);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        service.createUser(user);
        return "redirect:/login";
    }
//login functions
    @GetMapping("/login")
    public String showLogin(Model model){
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute("user")  User user, Model model, HttpSession session){
        User userExisting = service.getUserByEmail(user.getEmail());
        if (userExisting != null && userExisting.getPassword().equals(user.getPassword())){
            session.setAttribute("loggedInUser",userExisting);
            return "redirect:/home";
        }
        else {
            model.addAttribute("error", "Invalid username or password");
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
    public String ProfileAccount(){
        return "profile_account";
    }

}
