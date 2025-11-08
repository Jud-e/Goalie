package com.example.goalie.controller;
import com.example.goalie.model.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("/")
    public String Home(){
        return "home";
    }

    @GetMapping("/signup")
    public String Signup(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/login")
    public String Login(){
        return "login";
    }
    @PostMapping("/login")
    public String login(){
        return "redirect:/";
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
