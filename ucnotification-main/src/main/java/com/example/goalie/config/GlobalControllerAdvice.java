//package com.example.goalie.config;
//
//import com.example.goalie.config.AppService;
//import com.example.goalie.model.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ModelAttribute;
//
//@ControllerAdvice
//@RequiredArgsConstructor
//public class GlobalControllerAdvice {
//
//    private final AppService appService;
//
//    /**
//     * Adds the currently logged-in user to every model automatically.
//     * Available in Thymeleaf as ${user}.
//     */
//    @ModelAttribute("user")
//    public User addLoggedInUser(@AuthenticationPrincipal UserDetails userDetails) {
//        if (userDetails != null) {
//            return appService.getUserByEmail(userDetails.getUsername());
//        }
//        return null; // no user logged in
//    }
//}
