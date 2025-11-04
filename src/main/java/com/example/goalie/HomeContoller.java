package com.example.goalie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
public class HomeContoller {
    @GetMapping("/")
    public String Home(){
        return "home";
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
}
