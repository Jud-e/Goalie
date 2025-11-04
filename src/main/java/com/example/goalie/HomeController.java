package com.example.goalie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class HomeController {
    @GetMapping("/")
    public String Home(){
        return "home";
    }

    @GetMapping("/signup")
    public String Signup(){
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
        return "faq";
    }
}
