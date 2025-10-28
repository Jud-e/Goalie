package com.example.goalie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeContoller {
    @RequestMapping("/")
    public String Home(){
        return "home";
    }
}
