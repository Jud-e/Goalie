package com.example.goalie.controller;
import com.example.goalie.service.AppService;
import com.example.goalie.model.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {
    private final AppService service;

    @GetMapping("/")
    public String Home(){
        return "redirect:/signup";
//        return "redirect:/profile_account";
//        return "home";
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




    //Profile section
    @GetMapping("/user/{id}/edit")
    public String ProfileAccount(@PathVariable Long id, Model model){
        model.addAttribute("user",service.getUser(id));
        return "profile_account";
    }
    //Update User Profile
    @PostMapping("/user/{id}")
    public String update(@PathVariable Long id,
                         @Valid
                         @ModelAttribute("user") User user,
                         BindingResult br,
                         RedirectAttributes ra,
                         HttpSession session) {
        if (br.hasErrors()) return "profile_account";
        user.setId(id);
        service.saveUser(user);
        User loggedIn = (User) session.getAttribute("loggedInUser");
        if (loggedIn != null && loggedIn.getId().equals(id)) {
            session.setAttribute("loggedInUser", user);
        }
        ra.addFlashAttribute("info", "User updated successfully.");
        return "redirect:/home";
    }

}
